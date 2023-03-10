package com.github.asadaguitar.fpinhttp.routes

import cats.*
import cats.data.*
import cats.implicits.*
import cats.effect.*

import io.circe.*
import io.circe.generic.auto.*

import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.circe.*

import com.github.asadaguitar.fpinhttp.AppError
import com.github.asadaguitar.fpinhttp.models.User
import com.github.asadaguitar.fpinhttp.models.User.implicits.{given, *}
import com.github.asadaguitar.fpinhttp.service.UserService
import com.github.asadaguitar.fpinhttp.service.UserService.CreateCommand
import com.github.asadaguitar.fpinhttp.routes.Validator
import com.github.asadaguitar.fpinhttp.routes.ValidationDsl
import cats.data.Validated.Valid
import cats.data.Validated.Invalid
import java.time.Instant
import com.github.asadaguitar.fpinhttp.service.UserService.FindByIdCommand
import com.github.asadaguitar.fpinhttp.AppError.ValidationError

object UsersRoutes:
  import codec.{given, *}
  import protocol.*
  import validator.{given, *}
  import share.codec.{given, *}

  private object protocol {
    case class FindByIdResponseJson(id: String, name: String, created_at: Instant)
    case class CreateRequestJson(id: String, name: String, password: String)
    case class CreateResponseJson(id: String)
  }

  private object codec {
    import org.http4s.circe.CirceEntityCodec._
    
    given [F[_]]: EntityEncoder[F, FindByIdResponseJson] = jsonEncoderOf[F, FindByIdResponseJson] 
    given [F[_]: Concurrent]: EntityDecoder[F, CreateRequestJson] = jsonOf[F, CreateRequestJson]
    given [F[_]]: EntityEncoder[F, CreateResponseJson] = jsonEncoderOf[F, CreateResponseJson]
  }

  private object validator {

    given [F[_]](using F: Monad[F]): Validator[F, String, UserService.FindByIdCommand] with
      def validate(a: String): F[ValidatedNel[ValidationError, FindByIdCommand]] = 
        F.pure {
          User.Id(a).toValidatedNel.map(UserService.FindByIdCommand.apply)
        }

    given [F[_]](using F: Monad[F]): Validator[F, CreateRequestJson, UserService.CreateCommand] with
      def validate(req: CreateRequestJson): F[ValidatedNel[AppError.ValidationError, CreateCommand]] =
        val CreateRequestJson(id, name, password) = req
        F.pure {
          (
            User.Id(id).toValidatedNel,
            User.Name(name).toValidatedNel,
            User.Password(password).toValidatedNel
          )
          .mapN(UserService.CreateCommand.apply)
        }
  }

  def apply[F[_]](US: UserService[F])(using F: Async[F]) =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    BadRequest.apply()

    HttpRoutes.of[F] {
      /** find user by user id */
      case GET -> Root / "users" / id =>
        ValidationDsl.validate[F, String, UserService.FindByIdCommand](id) { cmd => 
          US.findById(cmd).value.flatMap {
            case Right(user) => 
              val User(id, name, _, _, createdAt, _, _) = user
              Ok(FindByIdResponseJson(id.show, name.show, createdAt))
            case Left(value) => BadRequest()
          }
        }

      /** create user */
      case req @ POST -> Root / "users" =>
        ValidationDsl
          .validateFromRequest[F, CreateRequestJson, UserService.CreateCommand](req){ 
            (cmd: UserService.CreateCommand) => 
              US.create(cmd).value.flatMap { 
                case Right(id) => 
                  Ok(CreateResponseJson(id = id.show))
                case Left(error) => 
                  BadRequest(error)
              }
          }
    }