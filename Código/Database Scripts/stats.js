print()

// quantidade de acórdãos

var numAcordaos = db.acordaos.stats().count;
print("quantidade de acordaos: " + numAcordaos + "\n");

// relator mais ativo

print("Os relatores mais ativos foram:\n")
db.acordaos.aggregate([
    { $group : { _id : "$relator", count: { $sum : 1} } },
    { $sort  : { count : -1 } },
    { $limit : 10 }
]).forEach( function(doc) {
    print(doc._id + " (" + doc.count + " acórdãos)")
})
print()

// quantidade de acórdãos que não fazem citações internas

var nonQuotingInside = db.links.find({quotes:[]}).count();
print("Quantidade de acordaos que nao citam um acordao da lista: " + nonQuotingInside + "\n");


// quantidade de acórdãos que fazem citações externas

var quotingOutside = db.links.find({quotes: [], quotesSomething:true}).count();
print(
      "Quantidade de acordaos que nao citam um acordao da lista, mas citam um fora da lista: " +
      quotingOutside +
      "\n"
);

// quantidade de acórdãos que não fazem nenhuma citação

var diff = nonQuotingInside - quotingOutside;
var percentage = 100*diff/numAcordaos;
var rounded = Math.round(percentage * 100) / 100;
print("Quantidade de acordaos que nao citam nenhum outro: " + diff + " (" + rounded + "%)\n");


// médias de citações

var quantity = 0;
var max = { quotes: [] };
db.links.find().forEach( function(link) {
    var length = link.quotes.length;
    quantity = quantity + length;
    if (length > max.quotes.length) {
        max = link;
    }
});
print("media de citacoes intenas por acordao: " + quantity / numAcordaos + "\n");
print("media de citacoes internas por acordao que cita: " + quantity/(numAcordaos - diff) + "\n");
print("media de citacoes internas por acordao que cita internamente: " + quantity/(numAcordaos - nonQuotingInside) + "\n");


// qual é o acordão que mais cita, e quantas citações faz

print("Acordao que mais cita: [" + max.file + "] " + max.id +" (" + max.quotes.length + " citacoes)\n");


// quais são os acórdãos mais citados

print("Os 5 acordaos mais citados sao:\n")
db.links.aggregate([
    { "$unwind": "$quotes" },
    { "$group":
        {
            "_id": "$quotes._id",
            "count": { "$sum": 1 }
        }
    },
    { "$sort": { "count": -1 } },
    { "$limit": 5 }
]).forEach( function(doc) {
    var ac = db.acordaos.findOne({ "_id" : doc._id });
    print(
          "[" + ac.file + "] " +
          ac.id + " - " +
          ac.uf + " - " +
          ac.relator + "\t- " +
          ("0" + ac.date.getDate()).slice(-2) + " / " + ("0" + (ac.date.getMonth()+1)).slice(-2) + " / " + ac.date.getFullYear() +
          " (" + doc.count + " citacoes)"
    );
})
print("\n")

// quais são os rótulos mais frequentes

map = function() {
    if (!this.tags) {
        return;
    }

    for (index in this.tags) {
        emit(this.tags[index], 1);
    }
}

reduce = function(previous, current) {
    var count = 0;

    for (index in current) {
        count += current[index];
    }

    return count;
}

db.runCommand({
    "mapReduce" : "acordaos",
    "map" : map,
    "reduce" : reduce,
    "out" : "tags",
})
print("Tags mais frequentes:\n")
db.tags.find().sort({ value: -1}).limit(10).forEach( function(doc) {
    print(doc.value + "\t" + doc._id)
})
print("\n")


// estados com mais acórdãos

print("Os estados com mais acórdãos são:\n")
db.acordaos.aggregate([
    { $group : { _id : "$uf", count : { $sum : 1} } },
    { $sort  : { count : -1 } },
    { $limit : 5 }
]).forEach( function(doc) {
    print(doc._id + " (" + doc.count + " acórdãos)")
})
print("\n")


// anos com mais acórdãos

print("Acórdãos por ano:\n")
db.acordaos.aggregate([
    { $group : {
        _id   : { $year : "$date" },
        count : { $sum  : 1} }
    },
    { $sort  : { count : -1 } }
]).forEach( function(doc) {
    print(doc._id + " (" + doc.count + " acórdãos)")
})
print("\n")
