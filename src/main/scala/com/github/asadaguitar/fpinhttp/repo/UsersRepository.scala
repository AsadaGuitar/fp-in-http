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
  import meta.{given, *}

  private[repo] object meta:

    given userIdMeta: Meta[User.Id] =
      Meta[String].imap(s => User.Id(s).unsafeGetValue)(i =>
        i.asInstanceOf[String]
      )

    given userNameMeta: Meta[User.Name] =
      Meta[String].imap(s => User.Name(s).unsafeGetValue)(i =>
        i.asInstanceOf[String]
      )

    given userPasswordMeta: Meta[User.Password] =
      Meta[String].imap(s => User.Password(s).unsafeGetValue)(i =>
        i.asInstanceOf[String]
      )

  end meta

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
    val User(id, name, password, isClosed, createdAt, modifiedAt, closedAt) =
      user
    sql"""
            INSERT INTO users(
                id, 
                name, 
                password,
                is_closed, 
                created_at, 
                modified_at, 
                closed_at
            ) VALUES (
                $id, 
                $name, 
                $password, 
                $isClosed, 
                $createdAt, 
                $modifiedAt, 
                $closedAt
            )
        """.update.run
  }
