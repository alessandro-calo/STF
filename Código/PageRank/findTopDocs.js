db.pageRanks.find(
    {},
    { _id : false, file : false}
).sort({
    pageRank : -1
}).limit(10).pretty()
