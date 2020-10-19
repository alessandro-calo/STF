# -*- coding: utf-8 -*-
from scrapy.selector import Selector
from scrapy.spider import BaseSpider
import html2text

class STFSpider(BaseSpider):

    name = 'stf'

    def __init__ ( self, page):
        self.domain = 'stf.jus.br'
        self.page = str(page).zfill(4)
        self.start_urls = [
        'http://www.stf.jus.br/portal/jurisprudencia/listarJurisprudencia.asp?'+
        's1=%28%40JULG+%3E%3D+'+
        '20010101'+                 # data inicial
        '%29%28%40JULG+%3C%3D+'+
        '20130101'+                 # data final
        '%29'+
        '&pagina='+ page +
        '&base=baseAcordaos'
        ]


    def parse( self, response ):
        print "\n\n\n\n"
        sel = Selector(response)
        body = sel.xpath(
            '/html/body/div[@id="pagina"]'+
            '/div[@id="conteiner"]'+
            '/div[@id="corpo"]'+
            '/div[@class="conteudo"]'+
            '/div[@id="divImpressao"]'+
            '/div[@class="abasAcompanhamento"]'
        )
        i = 1
        for tag in body:
            div = tag.xpath('div[@class="processosJurisprudenciaAcordaos"]').extract()[0]
            page = self.page
            jurisprudencia = str(i).zfill(2)
            f = open( 'P'+page+'J'+jurisprudencia, 'w' )
            f.write(html2text.html2text( div ).encode('utf-8'))
            i += 1
