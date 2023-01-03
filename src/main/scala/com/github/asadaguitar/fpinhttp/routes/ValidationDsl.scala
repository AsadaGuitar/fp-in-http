package com.github.asadaguitar.fpinhttp.routes

import com.github.asadaguitar.fpinhttp.routes.Validator

import org.http4s.*
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

import io.circe.generic.auto.*

import cats.*
import cats.data.*
import cats.implicits.*
import cats.data.Validated.*
import cats.effect.*

import share.codec.{given, *}

import org.http4s.dsl.Http4sDsl

object ValidationDsl:

  def validate[F[_]: Async, A, B](a: A)(
      f: B => F[Response[F]]
  )(using validator: Validator[F, A, B]): F[Response[F]] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    for
      v <- validator.validate(a)
      r <- {
        v match
          case Valid(a)   => f(a)
          case Invalid(e) => BadRequest(e)
      }
    yield r
  }

  def validateFromRequest[F[_]: Async, A, B](req: Request[F])(
      f: B => F[Response[F]]
  )(using
      decoder: EntityDecoder[F, A],
      validator: Validator[F, A, B]
  ): F[Response[F]] =
    req.as[A].flatMap(this.validate(_)(f))
