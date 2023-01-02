package com.github.asadaguitar.fpinhttp.models

import java.time.Instant
import com.github.asadaguitar.fpinhttp.AppError

import cats.*
import cats.implicits.*

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
      *     @hello
      *     - head: '@'
      *     - length: 6 ~ 20
      */
    opaque type Id = String
    
    /** example:
      *     hello
      *     - length: 4 ~ 20
      */
    opaque type Name = String

    /**
      * あとでちゃんとしたのにする
      */
    opaque type Password = String

    object Id:
        def apply(value: String): Either[AppError.ValidationError, Id] = 
            if value.startsWith("@") && 5 < value.length && value.length < 21 then
                value.asRight
            else 
                AppError.ValidationError("Invalid user ID value.").asLeft

    object Name:
        def apply(value: String): Either[AppError.ValidationError, Name] = 
            if 3 < value.length && value.length < 21 then
                value.asRight
            else 
                AppError.ValidationError("Invalid user name value.").asLeft
    
    object Password:
        def apply(value: String): Either[AppError.ValidationError, Password] = 
            if 3 < value.length && value.length < 21 then
                value.asRight
            else 
                AppError.ValidationError("Invalid user name value.").asLeft
    