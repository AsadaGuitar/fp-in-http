package com.github.asadaguitar.fpinhttp.repo

import doobie._
import doobie.implicits._
import doobie.implicits.javatime.*

import cats.implicits.*
import cats.effect.IO

import scala.concurrent.ExecutionContext

import java.time.*

import com.github.asadaguitar.fpinhttp.models.User
import com.github.asadaguitar.fpinhttp.models.User.*

import com.github.asadaguitar.fpinhttp.implicits.*

object UsersRepository:
  import share.{given, *}
  import User.implicits.{given, *}

  def existsById(id: User.Id): ConnectionIO[Boolean] = {
    sql"""
        SELECT EXISTS (
          SELECT *
          FROM users
          WHERE id = $id
        )
        """.query[Boolean].unique
  }

  def findById(id: User.Id): ConnectionIO[Option[User]] = {
    sql"""
        SELECT 
          id,
          name, 
          password, 
          is_closed, 
          created_at, 
          modified_at, 
          closed_at 
        FROM users 
        WHERE id = $id
        """.query[User].option
  }

  def insert(user: User): ConnectionIO[Int] = {
    val User(id, name, password, _, createdAt, _, _) = user
    sql"""
        INSERT INTO users(
          id, 
          name, 
          password, 
          created_at
        ) VALUES (
          $id, 
          $name, 
          $password, 
          $createdAt, 
        )
        """.update.run
  }
