package metamapperbug.snippet

import _root_.net.liftweb._
import http._
import SHtml._
import common._
import util.BindHelpers._
import metamapperbug.model._
import _root_.scala.xml._

class Zaphod {
  
  private object beeblebroxReqvar extends RequestVar[Box[Beeblebrox]](Empty)

  def hoopyFrood(xhtml : NodeSeq) : NodeSeq = {
    def save() = {
      val b: Beeblebrox = beeblebroxReqvar.is.open_!
      b.validate match {
        case Nil => b.save; S.notice("Saved!")
        case e => S.error(e); beeblebroxReqvar(Full(b))
      }
    }
    
    val b: Beeblebrox = beeblebroxReqvar.is match {
      case Empty => {
        beeblebroxReqvar(Full(Beeblebrox.create))
        beeblebroxReqvar.is.open_!
      }
      case Full(b) => b 
    }
    
    
    bind("z", xhtml,
      "id" -> text(b.id.toString, { id => b.id(id.toLong) }),
      "answer" -> text(b.answer, b.answer(_)),
      "save" -> submit("Save", () => { beeblebroxReqvar(Full(b)); save })
    )
  }
}
