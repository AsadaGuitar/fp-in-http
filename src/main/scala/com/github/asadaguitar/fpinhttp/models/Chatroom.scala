package com.github.asadaguitar.fpinhttp.models

import cats.*
import cats.implicits.*

import doobie.*
import doobie.implicits.*

import com.github.asadaguitar.fpinhttp.implicits.*

import java.time.Instant
import com.github.asadaguitar.fpinhttp.AppError

final case class Chatroom(
    id: Chatroom.Id,
    ownerId: User.Id,
    name: Chatroom.Name,
    isClosed: Boolean,
    createdAt: Instant,
    modifiedAt: Option[Instant],
    closedAt: Option[Instant]
)

object Chatroom:

  /** example:
    * 1234-5678-9123-4567
    */
  opaque type Id = String
  opaque type Name = String

  object Id:
    val pattern = "^(\\d{4}\\-){3}\\d{4}$".r
    def apply(value: String): Either[AppError.ValidationError, Id] =
      if pattern.matches(value) then value.asRight
      else AppError.ValidationError("Invalid chatrooom ID value.").asLeft

    /** for doobie */
    val chatroomIdMeta: Meta[Chatroom.Id] =
      Meta[String].imap(s => Chatroom.Id(s).unsafeGetValue)(i =>
        i.asInstanceOf[String]
      )

  object Name:
    def apply(value: String): Either[AppError.ValidationError, Name] =
      if 3 < value.length && value.length < 21 then value.asRight
      else AppError.ValidationError("Invalid user name value.").asLeft

    /** for doobie */
    val chatroomNameMeta: Meta[Chatroom.Name] =
      Meta[String].imap(s => Chatroom.Name(s).unsafeGetValue)(i =>
        i.asInstanceOf[String]
      )

  object implicits:
    given Meta[Id] = Id.chatroomIdMeta
    given Meta[Name] = Name.chatroomNameMeta