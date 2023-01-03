package com.github.asadaguitar.fpinhttp.repo

import doobie._
import doobie.implicits._

import cats.effect.*
import cats.effect.implicits.*

object DriverManager:

    def transactor[F[_]](using F: Async[F]) = Transactor.fromDriverManager[F](
        "org.postgresql.Driver", 
        "jdbc:postgresql:chatroom", 
        "developer", 
        "p@ssw0rd"
    )