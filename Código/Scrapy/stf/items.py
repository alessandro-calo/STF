from scrapy.item import Item, Field

class StfItem(Item):
    titulo      = Field()
    publicacao  = Field()
    partes      = Field()
    ementa      = Field()
    decisao     = Field()
    indexacao   = Field()
    legislacao  = Field()
    observacao  = Field()
    acordaos    = Field()
    tudo        = Field()
