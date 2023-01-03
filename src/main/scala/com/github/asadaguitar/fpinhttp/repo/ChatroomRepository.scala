package com.github.asadaguitar.fpinhttp.repo

import com.github.asadaguitar.fpinhttp.repo.data.Chatroom

import doobie.*
import doobie.implicits.*

object ChatroomRepository:
  import share.{given, *}
  import com.github.asadaguitar.fpinhttp.models.User.implicits.{given_Meta_Id}
  import com.github.asadaguitar.fpinhttp.models.Chatroom.implicits.{given, *}

  def insert(chatroom: Chatroom): ConnectionIO[Int] = {
    val Chatroom(id, ownerId, name, _, createdAt, _, _) = chatroom
    sql"""
        INSERT INTO chatroom(
          id, owner_id, name, created_at
        ) VALUES (
          $id, $ownerId, $name, $createdAt
        )
    """.update.run
  }
    