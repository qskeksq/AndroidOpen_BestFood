var express = require('express');
var formidable = require('formidable');
var db = require('../db')
var router = express.Router();


// keep/:member_seq/:info_seq
router.post('/:member_seq/:info_seq', function(request, response, next){
    
    var member_seq = request.params.member_seq;
    var info_seq = request.params.info_seq;
    console.log("member_seq : "+member_seq)
    console.log("info_seq : "+info_seq)    

    var select_sql = "select count(*) as cnt from bestfood_keep where member_seq = ? and info_seq = ? ";
    var insert_sql = "insert into bestfood_keep (member_seq, info_seq) values (?, ?);";
    // bestfood_info 에서 keep_cnt 를 변경해준다 
    var update_sql = "update bestfood_info set keep_cnt = keep_cnt+1 where member_seq = ?";
    var params = [member_seq, info_seq];

    db.get().query(select_sql, params, function(err, rows){
        if(err){
            return response.status(400).send("select_sql");
        }
        if(rows[0].cnt > 0){
            console.log("이미 있습니다")
            return response.sendStatus(400);
        }
        console.log("select_sql");
        db.get().query(insert_sql, params, function(err, rows){
            if(err){
                console.log(err);
                return response.status(400).send("insert_sql");
            }
            console.log("insert_sql");            
            db.get().query(update_sql, member_seq, function(err, next){
                if(err){
                    console.log(err);                    
                    return response.status(400).send("update_sql");
                }
                console.log("update_sql");                            
                response.sendStatus(200);
            });
        });
    });
});

// keep/:member_seq/:info_seq
router.delete('/:member_seq/:info_seq', function(request, response, next){

    var member_seq = request.params.member_seq;
    var info_seq = request.body.info_seq;
    console.log("member_seq : "+member_seq)
    console.log("info_seq : "+info_seq)    

    var sql_delete = "delete from bestfood_keep where member_seq = ? and info_seq = ?";
    var sql_update = "update bestfood_info set keep_cnt = keep_cnt-1 where seq = ?";

    db.get().query(sql_delete, [member_seq, info_seq], function(err, rows){
        if(err){
            console.log("sql_delete");
            return response.sendStatus(400);
        }
        db.get().query(sql_update, [info_seq], function(err, rows){
            if(err){
                console.log("sql_update");
                return response.sendStatus(400);
            }
        })
    })


});



// 하나라도 exports 설정해 주지 않으면 서버 자체가 실행되지 않는다.
module.exports = router;