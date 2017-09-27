var express = require('express');
var formidable = require('formidable');
var db = require('../db')
var router = express.Router();

//member/:phone
router.get('/:phone', function(req, res, next) {
  // express 모듈을 사용하면 /:를 통해서 클라이언트에서 주소를 통해 요청한 값을 params로 가져올 수 있다
  var phone = req.params.phone;
  
  // 요청한 번호로 서버에서 select한다.
  var sql = "select * from bestfood_member where phone = ? limit 1;";  
  console.log("sql : " + sql);    
  
	db.get().query(sql, phone, function (err, rows) {
      console.log("rows : " + JSON.stringify(rows));
      console.log("row.length : " + rows.length);
      // 서버에 값이 있다면 response로 성공을 보내고
      if (rows.length > 0) {
        res.status(200).json(rows[0]);
      // 값이 없으면 response에 오류를 보낸다. 참고로 이는 클라이언트에서 값을 따로 처리하기 위해
      // response에 값을 지정해 준 것이다.
      } else {
        res.sendStatus(400);
      }
  });
});

//member/phone
router.post('/phone', function(req, res) {
  // 레트로핏에서 @Field에 값을 담아서 보냈기 때문에 request의 body에서 꺼내 사용하는 것.
  // 필드는 키값의 형태로 가져오는 것이기 때문에 req와 같은 다른 값은 가져올 수 없다.
  var phone = req.body.phone;
  var sql_count = "select count(*) as cnt from bestfood_member where phone = ?;";  
  var sql_insert = "insert into bestfood_member (phone) values(?);";
  
  console.log("sql_count : " + sql_count);
  console.log("다른 값 : "+req.body.seq);
  console.log("phone : "+phone);
  
  // 전화번호를 쿼리해서
  db.get().query(sql_count, phone, function (err, rows) {
    console.log(rows);
    console.log(rows[0].cnt);

    // 이미 값이 있으면 response에 오류를 리턴
    if (rows[0].cnt > 0) {
      console.log("이미 가입되었습니다");
      return res.sendStatus(400);
    }

    // 값이 없으면 서버에 저장하고 resultId값을 리턴
    db.get().query(sql_insert, phone, function (err, result) {
      if (err) {
        return res.sendStatus(400);
      }
      console.log("result : "+result.insertId);
      res.send('' + result.insertId);
    });
  });
});

//member/info
router.post('/info', function(req, res) {
  // 레트로핏에서 @Body로 객체를 보내줬고, 확실하지는 않지만 
  // 컨버터팩토리를 사용했기 때문에 분리되어서 꺼낼 수 있는 듯 하다
  // 그런게 아니라 노드 서버에서 body-parser 모듈을 사용했기 때문에 json으로 전달되는 데이터가
  // 분리되어 들어오는 것이다.
  var phone = req.body.phone;
  var name = req.body.name;
  var sextype = req.body.sextype;
  var birthday = req.body.birthday;

  console.log({name, sextype, birthday, phone});

  var sql_count = "select count(*) as cnt from bestfood_member where phone = ?;";
  var sql_insert = "insert into bestfood_member (phone, name, sextype, birthday) values(?, ?, ?, ?);";
  var sql_update = "update bestfood_member set name = ?, sextype = ?, birthday = ? where phone = ?; ";
  var sql_select = "select seq from bestfood_member where phone = ?; ";
  
  // 서버에서 전화번호를 검색해서
  db.get().query(sql_count, phone, function (err, rows) {
    // 값이 있는 경우
    if (rows[0].cnt > 0) {
      console.log("sql_update : " + sql_update);
      // 업데이트
      db.get().query(sql_update, [name, sextype, birthday, phone], function (err, result) {
        if (err) return res.sendStatus(400);
        console.log(result);
        // 업데이트 된 값의 seq를 띄워준다
        db.get().query(sql_select, phone, function (err, rows) {
          if (err) return res.sendStatus(400);

          res.status(200).send("" + rows[0].seq);
        });
      });
    // 값이 없는 경우  
    } else {
      console.log("sql_insert : " + sql_insert);
      // 값을 저장
      db.get().query(sql_insert, [phone, name, sextype, birthday], function (err, result) {
        if (err) return res.sendStatus(400);

        res.status(200).send('' + result.insertId);
      });
    }
  });
});



//member/icon_upload
router.post('/icon_upload', function (req, res) {
  var form = new formidable.IncomingForm();

  // fileBegin로 이벤트를 on 시킨다는 의미
  form.on('fileBegin', function (name, file){
    // 파일이 들어오기 시작할 때
    // 내가 원하는 경로에 파일이 저장되도로 하기 위해 경로 지정
    file.path = './public/img/' + file.name;
  });

  form.parse(req, function(err, fields, files) {
    var sql_update = "update bestfood_member set member_icon_filename = ? where seq = ?;";
    console.log("sql_update : "+sql_update);

    db.get().query(sql_update, [files.file.name, fields.member_seq], function (err, rows) {
      res.sendStatus(200);
    });
  });
});

// 하나라도 exports 설정해 주지 않으면 서버 자체가 실행되지 않는다.
module.exports = router;