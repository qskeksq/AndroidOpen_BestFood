var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var db = require('./db');

/**
 * 1.2 라우트 코드를 로딩하는 코드
 */
var member = require('./routes/member');
var food = require('./routes/food');
var keep = require('./routes/keep');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

/**
 * 3. 별도 파일에서 라우트 함수를 작성할 때는 express.Router() 함수를통해 호출한다
 * module.exports = router 을 해주면 해당 파일에서 선언한 함수를 router를 통해서 사용할 수 있다.
 */
app.use('/member', member);
app.use('/food', food);
app.use('/keep', keep);

/**
 * 데이터베이스 연결
 */
db.connect(function(err){
  if(err){
    console.log('데이터베이스에 접속할 수 없습니다');
    process.exit(1);
  }
});

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
