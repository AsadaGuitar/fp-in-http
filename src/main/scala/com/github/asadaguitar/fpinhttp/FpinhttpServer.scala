package com.github.asadaguitar.fpinhttp

import cats.effect.Async
import cats.syntax.all._
import com.comcast.ip4s._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import com.github.asadaguitar.fpinhttp.service.UserService
import com.github.asadaguitar.fpinhttp.routes.UsersRoutes

object FpinhttpServer:

  def run[F[_]: Async]: F[Nothing] = {
    for
      client <- EmberClientBuilder.default[F].build
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)
      userAlg = UserService.impl[F](repo.DriverManager.transactor[F])

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        FpinhttpRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        FpinhttpRoutes.jokeRoutes[F](jokeAlg) <+>
        UsersRoutes[F](userAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <- 
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    yield ()
  }.useForever
