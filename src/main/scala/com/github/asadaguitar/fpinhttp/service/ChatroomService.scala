package com.github.asadaguitar.fpinhttp.service

import com.github.asadaguitar.fpinhttp.models.Chatroom
import com.github.asadaguitar.fpinhttp.models.User

import ChatroomService.*
import com.github.asadaguitar.fpinhttp.AppError

import cats.data.*

trait ChatroomService[F[_]]:
  def create(cmd: CreateCommand): EitherT[F, CreateError, Chatroom.Id]
  def findById(cmd: FindByIdCommand): EitherT[F, FindByIdError, Chatroom]

object ChatroomService:

  type CreateError = AppError.AlreadyExistsError | AppError.NotFoundError
  case class CreateCommand(
    id: Chatroom.Id,
    ownerId: User.Id, 
    name: Chatroom.Name
  )

  type FindByIdError = AppError.NotFoundError
  case class FindByIdCommand(id: Chatroom.Id)

