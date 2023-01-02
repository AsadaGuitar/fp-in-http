package com.github.asadaguitar.fpinhttp.repo

import doobie._
import doobie.implicits._
import doobie.implicits.javasql.*

import cats.effect.IO
import scala.concurrent.ExecutionContext

import java.time.*
import java.sql.Timestamp

object share:

    given instantMeta: Meta[Instant] =  Meta[Timestamp].imap
        (t => t.toLocalDateTime.atZone(ZoneOffset.UTC).toInstant)
        (i => Timestamp.valueOf(LocalDateTime.ofInstant(i, ZoneOffset.UTC)))