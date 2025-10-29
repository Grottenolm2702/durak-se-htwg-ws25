package de.htwg.DurakApp


import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers._


class RenderTUIMainSpec extends AnyWordSpec with Matchers{
    "THE MAIN" should {
        "run without errors" in {
            RenderTUI.main(Array())
        }
    }
}
