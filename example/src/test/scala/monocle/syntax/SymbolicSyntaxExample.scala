package monocle.syntax

import monocle.macros.GenLens
import monocle.{Lens, MonocleSuite, Prism}

class SymbolicSyntaxExample extends MonocleSuite {

  case class Store(articles: List[Article])

  sealed trait Article
  case class Table(wood: String) extends Article
  case class Sofa(color: String, price: Int) extends Article

  val _articles = Lens((_: Store).articles)(as => s => s.copy(articles = as))
  val _sofa     = Prism[Article, Sofa ]{ case s: Sofa  => Some(s); case _ => None}(identity)

  val sofaGenLens = GenLens[Sofa]
  val (_color, _price) = (sofaGenLens(_.color), sofaGenLens(_.price))


  test("Symbols can replace composeX and applyX methods") {
    val myStore = Store(List(Sofa("Red", 10), Table("oak"), Sofa("Blue", 26)))

    (_articles ^|-? headOption ^<-? _sofa ^|-> _color).getOption(myStore) shouldEqual
      (myStore &|-> _articles ^|-? headOption ^<-? _sofa ^|-> _color getOption)


    (_articles ^<-> iListToList.reverse ^|->> each ^<-? _sofa ^|-> _price).modify(_ / 2)(myStore) ===
    (myStore &|-> _articles ^<-> iListToList.reverse ^|->> each ^<-? _sofa ^|-> _price modify(_ / 2))

    (myStore.articles &|-? index(1) ^<-? _sofa getOption) shouldEqual None
  }
  
}
