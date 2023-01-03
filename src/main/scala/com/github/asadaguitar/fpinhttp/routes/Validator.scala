package com.github.asadaguitar.fpinhttp.routes

import cats.data.EitherT
import com.github.asadaguitar.fpinhttp.AppError

import cats.data.*

trait Validator[F[_], A, B]:
  def validate(a: A): F[ValidatedNel[AppError.ValidationError, B]]

object Validator:
  def apply[F[_], A, B](using ev: Validator[F, A, B]) = ev