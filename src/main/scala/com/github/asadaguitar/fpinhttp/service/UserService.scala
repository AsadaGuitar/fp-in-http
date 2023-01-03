package com.github.asadaguitar.fpinhttp.service

import com.github.asadaguitar.fpinhttp.models.User
import com.github.asadaguitar.fpinhttp.AppError
import com.github.asadaguitar.fpinhttp.Done
import com.github.asadaguitar.fpinhttp.service.UserService.*
import com.github.asadaguitar.fpinhttp.repo.UsersRepository

import cats.*
import cats.data.*
import cats.implicits.*

import cats.effect.*

import doobie.*
import doobie.implicits.*

trait UserService[F[_]]:
  def create(cmd: CreateCommand): EitherT[F, CreateError, User.Id]
  def findById(cmd: FindByIdCommand): EitherT[F, FindByIdError, User]

object UserService:

  type CreateError = AppError.AlreadyExistsError
  case class CreateCommand(
      id: User.Id,
      name: User.Name,
      password: User.Password
  )

  type FindByIdError = AppError.NotFoundError
  case class FindByIdCommand(id: User.Id)

  def impl[F[_]](xa: Transactor[F])(using sync: Sync[F]) = new UserService[F] {

    def create(cmd: CreateCommand): EitherT[F, CreateError, User.Id] = {
      val CreateCommand(id, name, password) = cmd
      EitherT {
        {
          for
            exists <- UsersRepository.existsById(id).transact(xa)
            _ <- {
              if exists then
                sync.raiseError(
                  AppError.AlreadyExistsError(s"Already exists $id")
                )
              else sync.unit
            }
            now <- sync.realTimeInstant
            user = User(
              id = id,
              name = name,
              password = password,
              isClosed = false,
              createdAt = now,
              modifiedAt = none,
              closedAt = none
            )
            _ <- UsersRepository.insert(user).transact(xa)
          yield id.asRight
        }
          .handleError {
            case e: CreateError => e.asLeft
            case e =>
              throw RuntimeException(
                s"An unexpected error has occurred, at `UserService.create` value e = $e."
              )
          }
      }
    }

    def findById(cmd: FindByIdCommand): EitherT[F, FindByIdError, User] = {
      val FindByIdCommand(id) = cmd
      EitherT {
        UsersRepository.findById(id).transact(xa).map {
          case None => AppError.NotFoundError(s"Not found user $id").asLeft
          case Some(value) => value.asRight
        }
      }
    }
  }
