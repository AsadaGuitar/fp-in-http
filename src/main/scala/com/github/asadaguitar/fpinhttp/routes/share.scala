package com.github.asadaguitar.fpinhttp.routes

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.*

import io.circe.*
import io.circe.syntax.*

import cats.data.*
import com.github.asadaguitar.fpinhttp.AppError

object share:

  type ErrorList = NonEmptyList[AppError]

  object codec:

    given [F[_]]: EntityEncoder[F, ErrorList] = jsonEncoderOf[F, ErrorList]

    given Encoder[ErrorList] with
      def apply(a: ErrorList): Json = Json.obj(
        "errors" -> a.toList.map(_.msg).asJson
      )

    given [F[_], A <: AppError]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

    given [A <: AppError]: Encoder[A] with
      def apply(a: A): Json = Json.obj(
        "errors" -> List(a.msg).asJson
      )

  end codec

