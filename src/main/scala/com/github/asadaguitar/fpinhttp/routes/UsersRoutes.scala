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

object UsersRoutes:
  import codec.{given, *}
  import protocol.*
  import validator.{given, *}
  import share.codec.{given, *}

  private object codec:
    import org.http4s.circe.CirceEntityCodec._

    given Encoder[User] with
      def apply(a: User): Json = Json.obj(
        "id" -> Json.fromString(a.id.asInstanceOf[String]),
        "name" -> Json.fromString(a.name.asInstanceOf[String]),
        "password" -> Json.fromString(a.password.asInstanceOf[String]),
        "is_closed" -> Json.fromString(a.isClosed.toString),
        "created_at" -> Json.fromString(a.createdAt.toString()),
        "modified_at" -> Json.fromString(a.modifiedAt.toString()),
        "closed_at" -> Json.fromString(a.closedAt.toString())
      )

    given [F[_]]: EntityEncoder[F, User] = jsonEncoderOf[F, User]

    given [F[_]: Concurrent]: EntityDecoder[F, CreateRequestJson] =
      jsonOf[F, CreateRequestJson]

    given [F[_]]: EntityEncoder[F, CreateResponseJson] = jsonEncoderOf[F, CreateResponseJson]
    
  end codec

  private object protocol:

    case class CreateRequestJson(id: String, name: String, password: String)
    case class CreateResponseJson(id: String)

  end protocol

  private object validator:

    given [F[_]](using
        F: Monad[F]
    ): Validator[F, CreateRequestJson, UserService.CreateCommand] with
      def validate(
          req: CreateRequestJson
      ): F[ValidatedNel[AppError.ValidationError, UserService.CreateCommand]] =
        val CreateRequestJson(id, name, password) = req
        F.pure {
          (
            User.Id(id).toValidatedNel,
            User.Name(name).toValidatedNel,
            User.Password(password).toValidatedNel
          )
            .mapN(UserService.CreateCommand.apply)
        }

  end validator

  def apply[F[_]](US: UserService[F])(using F: Async[F]) =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    BadRequest.apply()

    HttpRoutes.of[F] {
      /** find user by user id */
      case GET -> Root / "users" / id =>
        User.Id(id) match
          case Left(value) => BadRequest("not")
          case Right(value) =>
            val cmd = UserService.FindByIdCommand(id = value)
            US.findById(cmd).value.flatMap {
              case Right(value) => Ok(value)
              case Left(value)  => BadRequest()
            }
      /** create user */
      case request @ POST -> Root / "users" =>
        ValidationDsl[F, CreateRequestJson, UserService.CreateCommand](
          request
        ) { (cmd: UserService.CreateCommand) => 
          US.create(cmd).value.flatMap { 
            case Right(id) => 
              Ok(CreateResponseJson(id = id.show))
            case Left(error) => 
              BadRequest(error)
          }
        }
      }