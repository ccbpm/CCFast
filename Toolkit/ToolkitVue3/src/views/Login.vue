<template>
   <Spin :spinning="loading" size="large" :tip="loadMsg">
        <div class="login-wrapper">
          <div class="sider">
            <img class="sider-logo" :src="CCLogo" alt="logo" />
            <div class="sider-title">驰骋BPM\低代码:Toolkit For Vue3</div>
            <div class="description">流程、表单、低代码,功能页面&API集成</div>
              <div class="description doc">  <a  href='https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=8095272&doc_id=31094' target='_blank'>在线文档</a></div>
              <div class="description login_test">
                <span>测试账号:</span>
                <span>admin 管理员, guobaogeng财务人员, liping人力资源部经理, zhoutianjiao开发部技术人员.</span>
              </div>
              <div class="description">驰骋前端演示地址:<a :href="vue3Url" target='_blank'> <span class="doc">{{vue3Url}}</span></a>  </div>
              <div class="url-show">驰骋BPM服务地址:<a :href="flowUrl" target='_blank'> <span class="doc">{{flowUrl}}</span></a>  </div>
              <div class="login-box">
                <Form name="basic" style="width: 100%" :model="formState">
                  <FormItem name="username" :rules="[{ required: true, message: '请输入用户名' }]">
                      <Input
                        placeholder="用户名"
                        autocomplete="username"
                        v-model:value="formState.username"
                      >
                        <template #prefix>
                          <UserOutlined style="margin-right: 8px;" />
                        </template>
                    </Input>
                  </FormItem>
                  <FormItem name="password" :rules="[{ required: true, message: '请输入密码' }]">
                    <Input
                      type="password"
                      placeholder="密码,默认为:123"
                      autocomplete="current-password"
                      v-model:value="formState.password"
                    >
                    <template #prefix>
                        <LockOutlined style="margin-right: 8px;" />
                      </template>
                  </Input>
                  </FormItem>
                  <FormItem>
                    <Button type="primary" @click="login('PC')" :loading="pcLoading" style="margin-bottom: 15px; width: 100%; background-color: #ed7d31;">
                      登录PC端
                    </Button>
                    <Button type="primary" @click="login('PC')" :loading="mLoading" style="width: 100%; background-color: #ed7d31">
                      登录移动端(开发中)
                    </Button>
                  </FormItem>
                </Form>
              </div>
          </div>
          <Tabs v-model:activeKey="activeKey" type="card"  style="width: calc(100% - 600px);height: 100%;background-color: #fff;">
              <TabPane key="1" tab="耦合模式" style="height: 100%;">
                  <div class="content1" style="width:100%; height: 100%;"></div>
              </TabPane>
              <TabPane key="2" tab="非耦合模式">
                  <div class="content2" style="width:100%; height: 100%;"></div>
              </TabPane>
          </Tabs>
          <!-- <div class="content"></div> -->
      </div>
    </Spin>
</template>
<script setup lang="ts">
import CCLogo from '@/assets/logo.png'
import { reactive, ref } from 'vue'
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { Button, Form, FormItem, Input, message, Spin,Tabs,TabPane } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import { AppConfig } from '@/Toolkit/AppConfig'
    interface FormState {
      username: string
      password: string
    }
    //前台地址
    const vue3Url = AppConfig.AppCenterHost;
    //后台地址
    const flowUrl = AppConfig.AppHost

    //加载状态 
    const pcLoading = ref(false);
    const mLoading = ref(false);

    const loading = ref(false);

    const activeKey = ref('1');

    const loadMsg = ref('启动后台服务：'+flowUrl);

    const formState = reactive<FormState>({
      username: 'admin',
      password: '123'
    })

    const login = async (type='PC') => {
      try {
        if (type === 'PC') pcLoading.value = true;
        else if (type === 'Mobile') mLoading.value = true;
        loading.value = true;
        const webUser = useUserStore()
        await webUser.login({
          username: formState.username,
          password: formState.password,
          goHomeType: '',
          loginType: type
        })
      } catch (err:any) {
        message.error(err.toString())
      } finally{
        pcLoading.value = false;
        mLoading.value = false;
        loading.value = false;
      }
    }


</script>

<style lang="less" scoped>
.login-wrapper {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  background-color: #f2f5f7;
}

.sider {
  width: 600px;
  background-image: url(@/assets/login_bg.jpg);
  height: 100%;
  flex-direction: column;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.sider-logo {
  height: 40px;
  object-fit: cover;
  position: absolute;
  top: 20px;
  left: 30px;
  z-index: 10;
}

.sider-title {
  font-size: 24px;
  color: white;
  width: 100%;
  box-sizing: border-box;
  padding-left: 50px;
}

.description {
  font-size: 14px;
  color: white;
  width: 100%;
  box-sizing: border-box;
  padding-left: 50px;
  margin-top: 20px;
}
.url-show{
  font-size: 14px;
  color: white;
  width: 100%;
  box-sizing: border-box;
  padding-left: 50px;
  margin-top: 10px;
}
.doc{
  font-size: 18px;
}

.login_test{
  display: flex;
  flex-direction: column;
}

.content1 {
  height: 100%;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url(../assets/login_toolkit1.png);
  background-repeat: no-repeat;
  background-size: auto 90%;
  background-position: 50% 50%;
  position: relative;
  background-color: #fff;
}
.content2 {
  height: 100%;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url(../assets/login_toolkit2.png);
  background-repeat: no-repeat;
  background-size: auto 90%;
  background-position: 50% 50%;
  position: relative;
  background-color: #fff;
}

.login-box {
  margin-top: 50px;
  width: 375px;
  height: 255px;
  border-radius: 12px;
  // position: absolute;
  // right: 10%;
  // bottom: 15%;
  padding: 25px;
  border: 1px solid #e4e4e4;
  /* background-color: #d4eaff; */
  box-shadow: 0px 0px 10px 0px #89808057;
}
:deep(.ant-tabs-content){
  height: 100%;
}
</style>
