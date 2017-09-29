# BestFood 앱

- 클라이언트(Android)-서버(Nodejs)-DB(MySql) 연동 이해
- Android
  - 기획, 설계 : 서버, DB와 연동하는 안드로이드의 Information Architectur, Data Flow 이해
  - 모듈화 : activity, fragment / item, Lib, remote / adapter, custom 분리
  - 네트워크 통신
- Nodejs
  - 서버 생성부터 데이터베이스, 클라이언트 연동, 통신 이해
  - 서버 생성 -> (클라이언트 요청) -> 라우팅 -> RESTful -> request 분석 -> CRUD -> (DB 반영) ->response
  - formidable 이미지 통신
- MySql
  - 클라이언트에 적합하게 데이터를 보내주기 위한 데이터베이스 설계의 이해
  - insert, select, delete, update 쿼리
  - 테이블 join
  - 다중 쿼리문

## 1. Database / MySql

#### 중심이 되는 데이터와 파생되는 데이터의 관계, 그 안에서 데이터를 조회하고 수정하기 위한 연결점 설정이 중요하다
- bestfood_member - 사용자 정보 테이블
  - 기기번호/전화번호로 서버에서 사용자 정보를 가져온다
  - seq : 회원 번호, 전체 회원 수 조회 NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - phone : 회원 고유 번호로 서버에서 사용자 정보를 조회할 때 사용
  - name, sextype, birthday, member_icon_filename, reg_date


- bestfood_info - 맛집 정보 테이블
  - bestfood_member.seq로 조회
  - seq : 맛집 번호
  - member_seq : 이 맛집을 저장한 회원 번호. 사용자마다 다른 맛집 정보를 서버에서 불러와야 하는데
  이 때 Lacuher에서 불러온 사용자 정보가 미리 저장 되어 있고, 사용자정보에 회원 번호가 있어 이 번호로
  현재 사용자가 저장한 맛집 정보를 불러온다.
  - name, tel, address, latitude, longitude, description, keep_cnt, reg_date


- bestfood_info_image - 맛집 이미지 테이블
  - '서버/filename'로 조회
  - seq : 저장된 이미지 번호
  - info_seq : 이미지를 파일로 저장는데 파일 이름이 '사용자번호(member_info의 seq)+"_"+System.currentTimeMillis()'
  - filename : 이미지를 파일로 저장는데 파일 이름이 '사용자번호(member_info의 seq)+"_"+System.currentTimeMillis()'이다
  서버로부터 이미지를 불러올 때는 따로 GET 하지 않고 서버의 이미지 폴더(http://192.168.1.68:3000/img/이미지이름)를 Picasso에
  설정해주면 서버의 이미지 폴더에서 알아서 이미지를 가져온다. 한가지 유의할 점은 시간이 걸릴 수 있으므로 스레드 처리를 해 줘야 하는 것이다
  - image_memo, reg_date


- bestfood_keep - 즐겨찾기 테이블
  - bestfood_info.seq, bestfood_info.member_seq로 조회
  - seq : 즐겨찾기 저장된 아이템 개수
  - info_seq/member_seq : 즐겨찾기를 저장할 때 bestfood_info(맛집 정보)로부터 member_seq(사용자번호), seq(글번호)를 저장해 두고 Join해서 데이터를 GET 한다.
  - reg_date : 즐겨찾기 등록일자


## 2. Server / Nodejs
- GET
  - @Path("")  -> request.params : 라우팅 할 때 사용
  - @Query("") -> request.query : 검색 조건
- POST
  - @FormUrlEncoded -> @Field -> request.body 에서 같은 이름으로 꺼내와야 함 : 키-값으로 넘길 경우
  - @Body -> request.body : 객체 전체를 넘길 경우
  - @MultiPart -> @Part(1.RequestBody 2.MultiPart.Part) : 1.String 2.파일 객체 넘길 때
- Nodejs express 모듈 사용 : npm install express -g, express, express web -e

#### 클라이언트 요청

```java
String BASE_URL = "http://192.168.1.68:3000";
String MEMBER_ICON_URL = BASE_URL+"/member/";
String IMAGE_URL = BASE_URL+"/img/";

//==================================================================================================
//    사용자 정보
//==================================================================================================

// 서버에서 번호로 계정 찾기
@GET("/member/{phone}")
Call<MemberInfoItem> selectMemberInfo(@Path("phone") String phone);

// 서버에 번호로 계정 등록
@FormUrlEncoded
@POST("/member/phone")
Call<ResponseBody> insertMemberPhone(@Field("phone") String phone);

// 서버에 사용자 계정 등록
@POST("/member/info")
Call<ResponseBody> insertMemberInfo(@Body MemberInfoItem memberInfoItem);

// 이미지를 서버에 업로드
@Multipart
@POST("/member/icon_upload")
Call<ResponseBody> uploadMemberIcon(@Part("member_seq")RequestBody memberseq, @Part MultipartBody.Part file);

//==================================================================================================
//    맛집 정보
//==================================================================================================

// 서버에 맛집 정보 업로드
@POST("/food/info")
Call<ResponseBody> insertFoodInto(@Body FoodInfoItem foodInfoItem);

// 맛집 이미지 업로드
@Multipart
@POST("/food/info/image")
Call<ResponseBody> uploadFoodImage(@Part("info_seq") RequestBody infoSeq,
                                  @Part("image_memo") RequestBody imageMemo,
                                  @Part MultipartBody.Part file
);

// 맛집 정보
@GET("/food/list")
Call<List<FoodInfoItem>> listFoodInfo(@Query("member_seq") int memberSeq,
                                     @Query("user_latitude") double userLatitude,
                                     @Query("user_longitude") double userLongitude,
                                     @Query("order_type") String orderType,
                                     @Query("current_page") int currentPage
);

// 맛집 정보
@GET("/food/list/{info_seq}")
Call<FoodInfoItem> selectFoodInfo(@Path("info_seq") int infoSeq, @Query("member_seq") int memberSeq);

//==================================================================================================
//    지도 정보
//==================================================================================================

// 지도 프래그먼트 필요한 정보
@GET("/map/list")
Call<List<FoodInfoItem>> listMap(@Query("member_seq") int memberSeq,
                                @Query("latitude") double latitude,
                                @Query("longitude") double longitude,
                                @Query("distance") int distance,
                                @Query("user_latitude") double userLatitude,
                                @Query("user_longitude") double userLongitude);

//==================================================================================================
//    즐겨찾기
//==================================================================================================

// 즐겨찾기 추가
@POST("/keep/{member_seq}/{info_seq}")
Call<ResponseBody> insertKeep(@Path("member_seq") int memberSeq, @Path("info_seq") int infoSeq);

// 즐겨찾기 삭제
@POST("/keep/{member_seq}/{info_seq}")
Call<String> deleteKeep(@Path("member_seq") int memberSeq, @Path("info_seq") int infoSeq);

// 즐겨찾기 페이지
@GET("/keep/list")
Call<ArrayList<KeepItem>> listKeep(@Query("member_seq") int memberSeq,
                                   @Query("user_latitude") double userLatitude,
                                   @Query("user_longitude") double userLongitude
);
```

### (1) 기본 설정

#### 데이터베이스 연결

```JavaScript
/**
 * db.js 파일로 따로 데이터베이스 관리를 해준다
 */
// 쿼리 할때마다 연결하지 않고 서버 연결시 미리 풀을 생성해 둔다.
exports.connect = function(done) {
    pool = mysql.createPool({
        connectionLimit: 100,
        host     : 'localhost',
        user     : 'root',
        password : 'mysql',
        database : 'bestfood'
    });
}

// get() 함수를 통해 db pool에 접근할 수 있도록 한다
exports.get = function() {
  return pool;
}
```
```JavaScript
/**
 * app.js 에서 서버 연결할 때 db도 같이 연결해준다
 */
db.connect(function(err){
  if(err){
    console.log('데이터베이스에 접속할 수 없습니다');
    process.exit(1);
  }
});
```

#### 라우팅 설정
```JavaScript
/**
 * 라우트할 모듈
 */
var member = require('./routes/member');
var food = require('./routes/food');
var keep = require('./routes/keep');
```
```JavaScript
/**
 * 3. 별도 파일에서 라우트 함수를 작성할 때는 express.Router() 함수를 통해 호출한다
 * module.exports = router 을 해주면 해당 파일에서 선언한 함수를 router를 통해서 사용할 수 있다.
 * 참고로 module.exports = router 해주지 않으면 app.use로 라우팅 할 때 오류가 생긴다. 필수조건
 */
app.use('/member', member);
app.use('/food', food);
app.use('/keep', keep);
```

#### 추가 설정

```JavaScript
/**
 * request.body에서 값을 바로 꺼내 사용할 수 있는 이유가 바로 body-parser모듈을 등록했기 때문이다
 */
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
```

### (2) memeber
- POST 전화번호
- POST 사용자 정보
- POST 사용자 프로필 이미지
- GET 사용자정보

#### 전화번호 검색

- URL을 통한 요청은 @Path로 요청
- request에서 params 꺼내는 구조
- 쿼리에서 body-parser에 의해 객체화된 rows가 리턴. rows는 배열로, 안에 요청한 데이터가 '일정한 형식'으로 넘어온다. JSON.stringify(rows)
- response로 넘겨줄 경우 json(rows[0])로 Json데이터를 넘겨준다. GsonConverterFactory가 객체로 바꿔서 response.body()에거 꺼내 쓸 수 있다
- 주로 GET은 DB에서 꺼낸 값을 Json 객체로 response에 넘겨준다.

```JavaScript
// 클라이언트에서
router.get('/:phone', function(req, res, next) {
  // express 모듈을 사용하면 /:를 통해서 클라이언트에서 주소를 통해 요청한 값을 params로 가져올 수 있다
  var phone = req.params.phone;

  // 요청한 번호로 서버에서 select한다.
  var sql = "select * from bestfood_member where phone = ? limit 1;";
  console.log("sql : " + sql);

	db.get().query(sql, phone, function (err, rows) {
      // rows로 객체가 넘어온다는 것을 알 수 있다. 따라서 stringify 해줘야 읽을수 있다
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
```
#### 저장 / 업데이트
- 객체 POST는 @Body로 요청
- 서버(Node)에서 ControlFlow 다루기 기본 이해
- 서버에서 body-parser 모듈을 사용했기 때문에 클아이언트에서 객체로 넘겨온 데이터가 json으로 분리되어 넘어온다. request.body로 꺼내 사용한다
- rows 대신 result를 받으면 resultId를 response로 넘겨줄 수 있다.
- post는 insertId 값을 리턴해준다. 즉, DB에 저장 되었음을 알려주는 값을 리턴해준다.

```JavaScript
//member/info
router.post('/info', function(req, res) {
  // 노드 서버에서 body-parser 모듈을 사용했기 때문에 클아이언트에서 객체로 넘겨온 데이터가
  // json으로 분리되어 들어오는 것이다.
  var phone = req.body.phone;
  var name = req.body.name;
  var sextype = req.body.sextype;
  var birthday = req.body.birthday;

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
```

#### 프로필 이미지 업로드
- @MultiPart 추가 어노테이션
- @Part로 RequestBody, MultiPart.Part 사용
- formidable 모듈 사용

```JavaScript
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
```

### (3) food

- POST 맛집 정보
- POST 맛집 이미지
- GET 맛집 정보 전체
- GET 맛집 정보 한 개

#### 맛집 전체 리스트 쿼리

- URL queryString 영역으로 넘어온다
- request query 영역에서 꺼내 사용

```JavaScript
//food/list
router.get('/list', function(req, res, next) {
    var member_seq = req.query.member_seq;
    var user_latitude = req.query.user_latitude || DEFAULT_USER_LATITUDE;
    var user_longitude = req.query.user_longitude || DEFAULT_USER_LONGITUDE;
    var order_type = req.query.order_type;
    var current_page = req.query.current_page || 0;

    var sql = "select * from bestfood_info where member_seq = ? and latitude = ? and longitude = ? limit ?, ?;";
    var params = [member_seq, user_latitude, user_longitude, 0, 10];

    db.get().query(sql, params, function (err, rows) {
        if (err) return res.sendStatus(400);
        console.log("rows : " + JSON.stringify(rows));
        res.status(200).json(rows);
    });
});
```

### (4) keep
- POST 즐겨찾기 추가
- DELETE 즐겨찾기 삭제

#### 즐겨찾기 삭제

```JavaScript
// keep/:member_seq/:info_seq
router.delete('/:member_seq/:info_seq', function(request, response, next){

    var member_seq = request.params.member_seq;
    var info_seq = request.body.info_seq;

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
```

## 3. Client / Android

### (1) 설계와 구현 흐름

0. 설계  
        - 아키텍쳐  
        - 데이터베이스 통신  
        - 서버 통신  
        - 모듈화  
        - 인터페이스 사용  
        - 액티비티, 프래그먼트, 뷰  
        - 커스텀

1. 화면과 데이터베이스 설계를 기반으로 Item 클래스 구현  
        - MemberItem  
        - FoodItem  
        - ImageItem    
        - KeepItem  
        - GetoItem과 같이 구현 중에 추가적으로 필요한 모델 객체는 따로 설계할 때가 아닌 구현하면서 만들어 주면 된다.  

2. 네트워크 설정  
        - ServiceGenerator  
        - IRemoteService, 각 URL  
        - RemoteLib

3. 액티비티, 프래그먼트, 뷰
   - 액티비티 설계 : 큰 단위로 넘어가야 할 화면은 액티비티로 설계  
        - MyApp
        - PermissionActivity  
        - IndexActivity  
        - MainActivity  
        - ProfileActivity  
        - ProfileIconActivity  
        - BestFoodRegisterActivity  
   - 프래그먼트 설계 : 빠른 화면 전환이 있을 경우 프래그먼트로 설계  
        - BestFoodListFragment  
        - BestFoodKeepFragment  
        - BestFoodMapFragment  
        - BestFoodRegisterMap, Input, Image Fragment

4. 구현
   - 1차 기본구현  
        - 액티비티, 프래그먼트 전환  
        - Dummy Data로 기본 구성 확인  
        - 기본 데이터 통신   
        - 서버 통신 확인  
        - 필요한 라이브러리 모듈화 

   - 2차 상세구현  
        - 실제 데이터 통신  
        - 실제 서버 통신  
        - 예외처리, null처리  
        - 비동기, 병렬 처리  
        - 함수 분리, 모듈화, 아키텍쳐 등 기저 설계 적용  
        - 상세 라이브러리화  
   - 3차 리팩토링

4. 라이브러리 모듈화  

### (2) Item

#### FoodItem
- SerializedName("") 설정 - 서버에서 찾을 때 사용할 이름
- Bundle 객체에 담기 위해 Parcelable 인터페이스 구현

```java
public int seq;
@SerializedName("member_seq") public int memberSeq;
public String name;
public String tel;
public String address;
public double latitude;
public double longitude;
public String description;
@SerializedName("reg_date") public String regDate;
@SerializedName("mod_date") public String modDate;
@SerializedName("user_distance_meter") public double userDistanceMeter;
@SerializedName("is_keep") public boolean isKeep;
@SerializedName("image_filename") public String imageFilename;
```

```java
// Parcelable 인터페이스 구현
public FoodInfoItem(Parcel in) {
        seq = in.readInt();
        memberSeq = in.readInt();
        name = in.readString();
        tel = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        description = in.readString();
        regDate = in.readString();
        modDate = in.readString();
        userDistanceMeter = in.readDouble();
        isKeep = in.readByte() != 0;
        imageFilename = in.readString();
}

public static final Creator<FoodInfoItem> CREATOR = new Creator<FoodInfoItem>() {
        @Override
        public FoodInfoItem createFromParcel(Parcel in) {
            return new FoodInfoItem(in);
        }

        @Override
        public FoodInfoItem[] newArray(int size) {
            return new FoodInfoItem[size];
        }
};

@Override
public int describeContents() {
        return 0;
}

@Override
public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(seq);
        dest.writeInt(memberSeq);
        dest.writeString(name);
        dest.writeString(tel);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(description);
        dest.writeString(regDate);
        dest.writeString(modDate);
        dest.writeDouble(userDistanceMeter);
        dest.writeValue(isKeep);
        dest.writeString(imageFilename);
}
```


### (3) 네트워크 설정

#### ServiceGenerator

```java
public static <S> S createService(Class<S> serviceClass){
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(IRemoteService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    return retrofit.create(serviceClass);
    
}
```

#### IRemoteService


### (4) 액티비티 & 프래그먼트 구현  

#### MyApp
  - 전역에서 사용할 객체 데이터 저장
  - MemeberInfo
  - FoodInfo

```java
public class MyApp extends Application {

    private MemberInfoItem memberInfoItem;
    private FoodInfoItem foodInfoItem;
    ...
  
}
```

#### PermissionActivity

#### IndexActivity

- 처음 설정이 복잡할 수 있다. 연결여부 확인, 서버에서 데이터 확인, 가입 여부, 새 데이터 설정, 넘어갈 페이지 설정
 1. 인터넷 연결 확인
 2. 전화번호, 기기번호 확인
 3. 기존 계정이 있는지 확인 -> 있으면 정보 저장 -> 메인으로 넘어감 // 각각 서버 통신 여부 확인
 4. 없으면 새로 정보 저장 -> 프로필 저장 페이지로 넘어감   // 각가 서버 통신 여부 확인

 ```java
// 인터넷 연결 확인 - 연결되지 않았다면 리턴
if (!RemoteLib.getInstance().isConnected(this)) {
    showNoService();
    Log.e("[index화면] 1. 인터넷 연결 확인", "실패");
    return;
}
 ```

 ```java
  /**
  * 사용자 계정 확인2. 가져온 정보 서버에서 확인
  */
private void selectMemberInfoFromServer(String phoneNumber){
    // 결국 Retrofit 생성 -> GET 요청으로 데이터 불러오기 -> 성공 여부에 따라 다음으로 넘어갈지 결정
    IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);
    Call<MemberInfoItem> call = remoteService.selectMemberInfo(phoneNumber);
    call.enqueue(new Callback<MemberInfoItem>() {
        @Override
        public void onResponse(Call<MemberInfoItem> call, Response<MemberInfoItem> response) {
            Log.e("[index화면] 3.1 계정 불러오기", "성공");
            MemberInfoItem item = response.body();
            if(response.isSuccessful() && !StringLib.getInstance().isBlank(item.name)){
                Log.e("[index화면] 3.2 계정 불러오기", "성공, 기존 계정 확인");
                setMemberInfo(item);
            } else {
                Log.e("[index화면] 3.2 계정 불러오기", "성공, 하지만 기존 계정 없음");
                goProfileActivity(item);
            }
        }
        @Override
        public void onFailure(Call<MemberInfoItem> call, Throwable t) {
            Log.e("[index화면] 3. 계정 불러오기", "서버 통신에 실패");
        }
    });
}
```

 ```java
 // 네트워크 파악에 시간이 걸릴 수 있기 때문에 1초 후 호출
Handler handler = new Handler();
handler.postDelayed(new Runnable() {
    @Override
    public void run() {
        startTask();
    }
}, 1200);
 ```

 ```java
 // 서버에서 가져온 데이터 전역에 설정
private void setMemberInfo(MemberInfoItem item){
    ((MyApp) getApplicationContext()).setMemberInfoItem(item);
    startMain();
}
 ```

 ```java
 /**
  * 계정이 없는 경우 새로 서버에 등록한다.
  */
private void insertMemberInfo(){
    String phoneNumber = EtcLib.getInstance().getPhoeNumber(this);

    final IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);

    Call<ResponseBody> call = remoteService.insertMemberPhone(phoneNumber);
    call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if(response.isSuccessful()){
                Toast.makeText(IndexActivity.this, "번호 등록 성공", Toast.LENGTH_SHORT).show();
                Log.e("[index화면] 4.2 계정 없음", "계정 저장 성공");
            } else {
                int statusCode = response.code();
                Toast.makeText(IndexActivity.this, statusCode+" 오류 발생", Toast.LENGTH_SHORT).show();
                ResponseBody errbody = response.errorBody();
                Log.e("[index화면] 4.2 계정 없음", "계정 저장 실패 "+statusCode);
                Log.e("[index화면] 4.2 계정 없음", errbody.toString());
            }
        }
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Toast.makeText(IndexActivity.this, "서버 통신에 실패했습니다", Toast.LENGTH_SHORT).show();
            Log.e("[index화면] 4.2 계정 없음", "계정 저장 서버 통신 실패");
        }
    });
}
 ```


 #### MainActivity

 ```java
// 프로필 설정에서 사진을 설정했다면 네비게이션에 사진 설정
if (StringLib.getInstance().isBlank(memberInfoItem.memberIconFilename)) {
    Picasso.with(this).load(R.drawable.ic_person).into(profileIconImage);
} else {
    Picasso.with(this)
            .load(IRemoteService.MEMBER_ICON_URL + memberInfoItem.memberIconFilename)
            .into(profileIconImage);
}

// 프로필 설정을 하지 않았다면 네이게이션에 이름 설정 요청
TextView nameText = (TextView) headerLayout.findViewById(R.id.name);
if (memberInfoItem.name == null || memberInfoItem.name.equals("")) {
    nameText.setText(R.string.name_need);
} else {
    nameText.setText(memberInfoItem.name);
}
 ```

 #### ProfileActivity

 ```java
/**
  * 기존 사용자 정보와 새로 입력한 사용자 정보를 비교해서 변경되었는지를 파악한다.
  * @param newItem 사용자 정보 객체
  * @return 변경되었다면 true, 변경되지 않았다면 false
  */
private boolean isChanged(MemberInfoItem newItem) {
    if (newItem.name.trim().equals(item.name)
            && newItem.sextype.trim().equals(item.sextype)
            && newItem.birthday.trim().equals(item.birthday)) {
        return false;
    } else {
        return true;
    }
}
```
```java
/**
  * 사용자가 이름을 입력했는지를 확인한다.
  * @param newItem 사용자가 새로 입력한 정보 객체
  * @return 입력하지 않았다면 true, 입력했다면 false
  */
private boolean isNoName(MemberInfoItem newItem) {
    if (StringLib.getInstance().isBlank(newItem.name)) {
        return true;
    } else {
        return false;
    }
}
 ```
 ```java
/**
  * 사용자가 입력한 정보를 저장한다.
  */
private void save() {
    final MemberInfoItem newItem = getMemberInfoItem();

    // 변경 사항이 없음
    if (!isChanged(newItem)) {
        Toast.makeText(this, "바뀐 내용이 없습니다", Toast.LENGTH_SHORT).show();
        return;
    }

    // 변경 사항 있을 경우
    IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);
    Call<ResponseBody> call = remoteService.insertMemberInfo(newItem);
    call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.isSuccessful()) {
                String seq = null;
                try {
                    seq = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("Response 리턴값", seq);
                try {
                    item.seq = Integer.parseInt(seq);
                    if (item.seq == 0) {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
                item.name = newItem.name;
                item.sextype = newItem.sextype;
                item.birthday = newItem.birthday;
                Log.e("프로필 설정", "성공");
                finish();
            } else {
                Log.e("프로필 설정", "오류");
            }
        }
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.e("프로필 설정", "서버 연결 실패");
        }
    });
}
 ```


 #### ProfileIconActivity

 ```java
/**
  * 이미지 파일 / 이미지 파일 이름 설
  */
private void setProfileIconFile(){
    // 파일 이름 설정
    profileIconFileName = memberInfoItem.seq+"_"+System.currentTimeMillis();

    // 파일 만들기
    profileIconFile = FileLib.getInstance().getProfileIconFile(this, profileIconFileName);
}
 ```