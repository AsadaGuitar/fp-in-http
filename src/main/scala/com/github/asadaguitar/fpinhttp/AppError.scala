package com.github.asadaguitar.fpinhttp

enum AppError(msg: String) extends Exception:
  case ValidationError(msg: String) extends AppError(msg)
  case AlreadyExistsError(msg: String) extends AppError(msg)
  case NotFoundError(msg: String) extends AppError(msg)
