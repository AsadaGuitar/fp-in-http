package com.github.asadaguitar.fpinhttp

sealed abstract class Done extends Serializable

case object Done extends Done:
  def getInstance(): Done = this
  def done(): Done = this