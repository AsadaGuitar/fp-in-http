package com.github.asadaguitar.fpinhttp

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import com.github.asadaguitar.fpinhttp.service.UserService
import com.github.asadaguitar.fpinhttp.models.User

import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

object FpinhttpRoutes:

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }

  given Encoder[User] with
    def apply(a: User): Json = Json.obj(
      "id" -> Json.fromString(a.id.asInstanceOf[String]),
      "name" -> Json.fromString(a.name.asInstanceOf[String]),
      "password" -> Json.fromString(a.password.asInstanceOf[String]),
      "is_closed" -> Json.fromString(a.isClosed.toString),
      "created_at" -> Json.fromString(a.createdAt.toString()),
      "modified_at" -> Json.fromString(a.modifiedAt.toString()),
      "closed_at" -> Json.fromString(a.closedAt.toString()),
    )

  given [F[_]]: EntityEncoder[F, User] = jsonEncoderOf[F, User]

  def usersRoutes[F[_]: Sync](U: UserService[F]) = 
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "users" / id => 
        User.Id(id) match
          case Left(value) => BadRequest()
          case Right(value) => 
            val cmd = UserService.FindByIdCommand(id = value)
            U.findById(cmd).value.flatMap {
              case Right(value) => Ok(value)
              case Left(value) => BadRequest()
            }
    }