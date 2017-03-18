### CoolWeb 我的Web项目，用来学习Web接口开发
<br>采用SpringMVC和MyBatis框架，没有涉及到js方面的东西（暂时还不会啊）</br>
###服务器地址：http://182.254.232.121:8080/CoolWeb/
###用户个人信息模块
####注册
<br>注册 register?phone=xxxx&password=xxx 备注:无限制 请求方式：Post</br>
<br>示例：http://182.254.232.121:8080/CoolWeb/register?phone=18696855784&password=555555</br>
<br>返回结果示例：
  {"status":"-1","errorCode":"10003","msg":"该号码已被注册","data":""}
</br>
####登录
<br>登录 login?phone=xxxxx&password=xxxx 备注：无 请求方式：Post</br>
<br>示例：http://182.254.232.121:8080/CoolWeb/login?phone=1869855784&password=555555</br>
<br>返回结果：</br>
<br>
{"status":"1","errorCode":"","msg":"登录成功","data":{"birthday":"1992-01-01","password":"555555","phone":"18696855784","sex":"男","user_id":"10000"}}
</br>
####修改用户信息
<br>修改用户信息 updateUserInfo 备注：无 请求方式：Post</br>
<br>请求示例：http://182.254.232.121:8080/CoolWeb/updateUserInfo?phone=18696855784&sex=男&birthday=1992-04-12&nickname=Vincent&live_status=今天在家写代码没有出门</br>
<br>返回结果：</br>
<br>
  {"status":"1","errorCode":"","msg":"个人资料修改成功","data":""}
</br>
#### 忘记密码
<br>忘记密码 resetPassword</br>
<br>请求示例：http://127.0.0.1:8080/CoolWeb/resetPassword?phone=18696855784&password=888888 备注：无 请求方式：Post</br>
<br>返回结果示例：</br>
<br>
 {"status":"1","errorCode":"","msg":"重置密码成功","data":""}
</br>
#### 修改密码
<br> 修改密码 alterUserPassword</br>
<br>请求示例： http://127.0.0.1:8080/CoolWeb/alterUserPassword?phone=18696855784&old_password=555555&new_password=888888 备注:无 请求方式： Post</br>
<br>返回结果：</br>
<br>
 {"status":"-1","errorCode":"10003","msg":"旧密码错误，请重试","data":""}
</br>
### 添加反馈
<br>添加反馈 addFeedback</br>
<br>请求示例： http://127.0.0.1:8080/CoolWeb/addFeedback?phone=18696855784&title=这个APP有bug&content=我的APP崩溃了 备注：无 请求方式：Post</br>
<br>返回结果示例：</br>
<br>
 {"status":"1","errorCode":"","msg":"反馈成功","data":""} 
</br>
### 获取反馈列表
<br>获取反馈列表 getAllFeedback</br>
<br>请求示例：http://127.0.0.1:8080/CoolWeb/getAllFeedback?phone=18696855784 备注 ：无 请求方式：Get</br>
<br>返回结果示例：</br>
<br>
{"status":"1","errorCode":"","msg":"获取反馈记录成功","data":[{"phone":"18696855784","createTime":"2017-03-08 00:46","title":"fsfs","content":"fffff"},{"phone":"18696855784","createTime":"2017-03-08 01:36:06","title":"这个APP有bug","content":"我的APP崩溃了"}]}
</br>
###日记模块
#### 添加日记
<br>添加日记 insertDiary</br>
<br>请求示例：http://182.254.232.121:8080/CoolWeb/insertDiary?user_id=10000&diaryTitle=WebProject&diaryContent=今天在家歇Web项目，采用SpringMVC和MyBatista框架，正在写文档。。。 备注：无 请求方式：Post</br>
<br>返回结果：</br>
<br>
{"status":"1","errorCode":"","msg":"日记已保存","data":""}
</br>
#### 删除某用户所有日记
<br>删除某用户所有日记 deleteUserAllDiary</br>
<br>请求示例：http://182.254.232.121:8080/CoolWeb/deleteUserAllDiary?user_id=10000 备注：无 请求方式：Post</br>
<br>返回结果：</br>
<br>
{"status":"1","errorCode":"","msg":"已删除所有日记","data":""}
</br>
#### 删除用户的某一条日记
<br>删除用户的某一条日记 deleteOneDiary</br>
<br>请求示例：http://182.254.232.121:8080/CoolWeb/deleteOneDiary?user_id=10000&diaryTitle=写代码了 备注：无 请求方式：Post</br>
<br>返回结果示例：</br>
<br>
  {"status":"1","errorCode":"","msg":"已删除","data":""}
</br>
#### 获取用户所有日记
<br>获取用户所有日记 getUserAllDiary</br>
<br>请求示例： http://182.254.232.121:8080/CoolWeb/getUserAllDiary?user_id=10000 备注：无 请求方式：GET</br>
<br>返回结果示例：</br>
<br>
  {"status":"1","errorCode":"","msg":"获取所有日记成功","data":[{"diaryTitle":"写代码","user_id":10000,"diaryContent":"CoolWeb的个人模板和日记模块接口编写","diaryTime":"2017-03-04 23:53:19"},{"diaryTitle":"写代码了","user_id":10000,"diaryContent":"CoolWeb的个人模板和日记模块接口编写","diaryTime":"2017-03-04 23:56:35"},{"diaryTitle":"没有出门","user_id":10000,"diaryContent":"CoolWeb的个人模板和日记模块接口编写","diaryTime":"2017-03-04 23:56:42"},{"diaryTitle":"卧槽","user_id":10000,"diaryContent":"CoolWeb的个人模板和日记模块接口编写","diaryTime":"2017-03-04 23:56:45"},{"diaryTitle":"我是指测试一下","user_id":10000,"diaryContent":"CoolWeb的个人模板和日记模块接口编写","diaryTime":"2017-03-04 23:56:52"}]}
</br>
### 房间信息
#### 添加房间
<br>添加房间 addRoom</br>
<br>请求示例：http://182.254.232.121:8080/CoolWeb/addRoom?phone=18696855784&roomType=卧室&roomName=我的单间&roomImg=ffffffffffffffffffffff&roomBigImg=fffffffffffffffffffffffffffffffffffffffffffffff 备注：无 请求方式:Post</br>
<br>返回结果示例：</br>
<br>{"status":"1","errorCode":"","msg":"Room已保存","data":{"id":8,"phone":"18696855784","roomBigImg":"fffffffffffffffffffffffffffffffffffffffffffffff","roomImg":"ffffffffffffffffffffff","roomName":"我的","roomType":"卧室"}}</br>
#### 删除房间
<br>删除房间 deleteRoomItem</br>
<br>请求接口示例：http://182.254.232.121:8080/CoolWeb/deleteRoomItem?phone=18696855784&roomName=我的单间 备注：无 请求方式：Post</br>
<br>返回结果示例：</br>
<br>{"status":"1","errorCode":"","msg":"Room信息已删除","data":""}</br>
#### 获取所有房间
<br>获取所有房间列表 getAllRoom</br>
<br>请求示例：http://182.254.232.121:8080/CoolWeb/getAllRoom?phone=18696855784 备注：无 请求方式：Get</br>
<br>返回结果示例：</br>
<br>{"status":"1","errorCode":"","msg":"已拿到相关数据","data":[{"phone":"18696855784","roomImg":"ffffffffffffffffffffff","id":1,"roomName":"我的","roomType":"卧室","roomBigImg":"fffffffffffffffffffffffffffffffffffffffffffffff"}]}</br>
<br></br>
