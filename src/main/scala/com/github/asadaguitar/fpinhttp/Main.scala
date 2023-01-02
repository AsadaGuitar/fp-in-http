package com.github.asadaguitar.fpinhttp

import cats.effect.{IO, IOApp}

import doobie._
import doobie.implicits._

object Main extends IOApp.Simple:
  val run = FpinhttpServer.run[IO]
  // val run = 
  //   repo.UsersRepository
  //     .findById("@asada")
  //     .transact(repo.DriverManager.transactor)
  //     .map(println)
