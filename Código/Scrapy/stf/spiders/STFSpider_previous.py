# -*- coding: utf-8 -*-
from scrapy.selector import Selector
from scrapy.spider import BaseSpider
from scrapy.http import Request
from stf.items import StfItem
import urlparse
import html2text

class STFSpider(BaseSpider):
    name = 'stf_previous'
    allowed_domains = ['stf.jus.br']
    start_urls = [
    'http://www.stf.jus.br/portal/jurisprudencia/listarJurisprudencia.asp?s1=%28%40JULG+%3E%3D+20010101%29%28%40JULG+%3C%3D+20130101%29&base=baseAcordaos'
    ]


    def parseItem( self, item ):
        text = html2text.html2text(item)
        # text = item.replace( '*',               '' )
        # text = text.replace( '\n\n',            '\n' )
        # text = text.replace( '    ',            '\n' )
        # text = text.replace( '&nbsp',           '' )
        # text = text.replace( 'ementa   \n  \n', '' )
        return text

    def parse( self, response ):
        print "\n\n\n\n"
        sel = Selector(response)
        body = sel.xpath('/html/body/div[@id="pagina"]/div[@id="conteiner"]/div[@id="corpo"]/div[@class="conteudo"]/div[@id="divImpressao"]/div[@class="abasAcompanhamento"]')

        for tag in body:
            div = tag.xpath('div[@class="processosJurisprudenciaAcordaos"]')

            titulo      = self.parseItem( div.xpath( 'p[1]' ).extract( )[0] )
            publicacao  = ''
            partes      = ''
            ementa      = ''
            decisao     = ''
            indexacao   = ''
            legislacao  = ''
            observacao  = ''

            sectionTags = [
                'p[2]',
                'p[3]',
                'strong/p/strong',
                'p[4]',
                'p[5]',
                'p[6]',
                'p[7]',
                'p[8]',
            ]

            for index, tag in enumerate(sectionTags):
                if div.xpath( tag ) != []:
                    secao = self.parseItem( div.xpath( tag ).extract()[0] )
                    print">>>=========== "+ secao,

                    if secao.startswith( 'Publica' ):
                        publicacao = self.parseItem( div.xpath('pre[1]').extract( )[0] )
                    elif secao.startswith( 'Parte' ):
                        partes = self.parseItem( div.xpath( 'pre[2]' ).extract( )[0] )
                    elif secao.startswith('Ementa'):
                        ementa = self.parseItem( div.xpath( 'strong/p' ).extract( )[0] )
                    elif secao.startswith('Decis'):
                        decisao = self.parseItem( div.xpath( 'pre[3]' ).extract( )[0] )
                    elif secao.startswith('Index'):
                        indexacao = self.parseItem( div.xpath( 'pre[4]' ).extract( )[0] )
                    elif secao.startswith('Legisl'):
                        legislacao = self.parseItem( div.xpath( 'pre[5]' ).extract( )[0] )
                    elif secao.startswith('Observ') and div.xpath( 'pre[6]' ).extract( ) != []:
                        observacao = self.parseItem( div.xpath( 'pre[6]' ).extract( )[0] )

            print '-------------------------------------------------------------------------------------'


            yield StfItem(
                titulo      = titulo,
                publicacao  = publicacao,
                partes      = partes,
                ementa      = ementa,
                decisao     = decisao,
                indexacao   = indexacao,
                legislacao  = legislacao,
                observacao  = observacao
            )


        # nextPage = body.xpath('../table/tr/td/table/tr/td[2]/p/span/a[7]/@href')[0].extract()
        # yield Request(urlparse.urljoin('http://www.stf.jus.br/portal/jurisprudencia/', nextPage), callback=self.parse)
