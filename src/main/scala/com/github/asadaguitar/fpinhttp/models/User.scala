package com.github.asadaguitar.fpinhttp.models

import java.time.Instant
import com.github.asadaguitar.fpinhttp.AppError

import cats.*
import cats.implicits.*

import doobie._
import doobie.implicits._

import com.github.asadaguitar.fpinhttp.implicits.*

case class User(
    id: User.Id,
    name: User.Name,
    password: User.Password,
    isClosed: Boolean,
    createdAt: Instant,
    modifiedAt: Option[Instant],
    closedAt: Option[Instant]
)

object User:

  /** example:
    * @hello
    *   - head: '@'
    *   - length: 6 ~ 20
    */
  opaque type Id = String

  /** example: hello
    *   - length: 4 ~ 20
    */
  opaque type Name = String

  /** あとでちゃんとしたのにする
    */
  opaque type Password = String

  object Id:
    def apply(value: String): Either[AppError.ValidationError, Id] =
      if value.startsWith("@") && 5 < value.length && value.length < 21 then
        value.asRight
      else AppError.ValidationError("Invalid user ID value.").asLeft

    /** for doobie */
    val userIdMeta: Meta[User.Id] =
      Meta[String].imap(s => User.Id(s).unsafeGetValue)(i =>
        i.asInstanceOf[String]
      )

  object Name:
    def apply(value: String): Either[AppError.ValidationError, Name] =
      if 3 < value.length && value.length < 21 then value.asRight
      else AppError.ValidationError("Invalid user name value.").asLeft

    /** for doobie */
    val userNameMeta: Meta[User.Name] =
      Meta[String].imap(s => User.Name(s).unsafeGetValue)(i =>
        i.asInstanceOf[String]
      )

  object Password:
    def apply(value: String): Either[AppError.ValidationError, Password] =
      if 3 < value.length && value.length < 21 then value.asRight
      else AppError.ValidationError("Invalid user name value.").asLeft

    /** for doobie */
    val userPasswordMeta: Meta[User.Password] =
      Meta[String].imap(s => User.Password(s).unsafeGetValue)(i =>
        i.asInstanceOf[String]
      )

  object implicits:
    given Show[Id]       = (t: Id) => t
    given Show[Name]     = (t: Name) => t
    given Show[Password] = (t: Password) => t
    given Meta[Id]       = Id.userIdMeta
    given Meta[Name]     = Name.userNameMeta
    given Meta[Password] = Password.userPasswordMeta
