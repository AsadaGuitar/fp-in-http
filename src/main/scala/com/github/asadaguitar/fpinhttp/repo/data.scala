package com.github.asadaguitar.fpinhttp.repo

import java.time.Instant
import cats.*
import cats.implicits.*
import cats.data.*

object data:

    case class Chatroom(
        id: String, 
        ownerId: String, // Users.id
        name: String,
        isClosed: Boolean = false,
        createdAt: Instant = Instant.now(),
        modifiedAt: Option[Instant] = none,
        closedAt: Option[Instant] = none
    )

    case class UserChatroom(
        id: String,
        userId: String, // Users.id
        chatroomId: String, // Chatrooms.id
        isClosed: Boolean = false,
        createdAt: Instant = Instant.now(),
        modifiedAt: Option[Instant] = none,
        closedAt: Option[Instant] = none
    )

    case class Message(
        id: String,
        chatroomId: String, // Chatrooms.id
        userId: String, // Users.id
        replyTo: Option[String], // Users.id
        text: String,
        isClosed: Boolean = false,
        createdAt: Instant = Instant.now(),
        modifiedAt: Option[Instant] = none,
        closedAt: Option[Instant] = none
    )

    case class Follow(
        id: String,
        from: String, // Users.id
        to: String, // Users.id
        isClosed: Boolean = false,
        createdAt: Instant = Instant.now(),
        modifiedAt: Option[Instant] = none,
        closedAt: Option[Instant] = none
    )

    case class Favorite(
        id: String,
        messageId: String, // Message.id
        userId: String, // Users.id
        isClosed: Boolean = false,
        createdAt: Instant = Instant.now(),
        modifiedAt: Option[Instant] = none,
        closedAt: Option[Instant] = none
    )

    case class Invitation(
        id: String,
        chatroomId: String, // Chatroom.id
        from: String, // Users.id
        to: String, // Users.id
        isClosed: Boolean = false,
        createdAt: Instant = Instant.now(),
        modifiedAt: Option[Instant] = none,
        closedAt: Option[Instant] = none
    )