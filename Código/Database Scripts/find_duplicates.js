db.duplicates.drop();
var previous;
db.acordaos.find( {}, {"id" : true, "file" : true} ).sort( { "id" : 1} ).forEach( function(current) {
    if (previous != null && current.id == previous.id) {
        var duplicate = {
            "id1"   : previous._id,
            "file1" : previous.file,
            "id2"   : current._id,
            "file2" : current.file
        };
        db.duplicates.insert(duplicate);
        db.acordaos.remove({ _id: duplicate.id1});
        db.acordaos.remove({ _id: duplicate.id2});
    }
    previous = current;
});
