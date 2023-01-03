package com.github.asadaguitar.fpinhttp

sealed trait AppError extends Exception:
  def msg: String

object AppError:
  case class ValidationError(msg: String) extends AppError
  case class AlreadyExistsError(msg: String) extends AppError
  case class NotFoundError(msg: String) extends AppError
