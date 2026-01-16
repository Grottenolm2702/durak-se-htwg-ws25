package de.htwg.DurakApp.testutil
import de.htwg.DurakApp.util.Observer
class SpyObserver extends Observer:
  private var _updateCount: Int = 0
  def update: Unit =
    _updateCount = _updateCount + 1
  def updateCount: Int = _updateCount
  def wasCalled: Boolean = _updateCount > 0
