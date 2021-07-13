<template>
  <div v-loading="loading">
    <div style="margin-top: 10px;display: flex;justify-content: center">
      <el-input
        placeholder="默认展示部分用户，可以通过用户名搜索用户..."
        prefix-icon="el-icon-search"
        v-model="keywords" style="width: 400px" size="small">
      </el-input>
      <el-button type="primary" icon="el-icon-search" size="small" style="margin-left: 3px" @click="searchClick">搜索
      </el-button>
    </div>

    <div style="width: 800px">
      <div v-for="(v,i) in contactlist">
        <div data-repeater-list="">
          <div data-repeater-item>
            <div class="form-group  m-form__group row" style="padding-top: 15px;padding-bottom: 15px;">
              <label class="col-form-label col-lg-2 col-sm-12">联系人<span
                style="color: #F00">*</span>
              </label>
              <div class="col-lg-3">
                <input type="text" v-model="contactlist[i].name" @change="onchangContactPersonName(i)"
                       class="form-control m-input&#45;&#45;fixed"
                       placeholder="请搜索联系人名称"/>
                <div class="select-panel">
                  <div v-show="contactlist[i].isShow" v-for="w in words" class="select-item" @click="click_item(w,i)">{{ w.NAME }}</div>
                </div>
              </div >
              <label class="col-form-label col-lg-2 col-sm-12">电话<span
                style="color: #ff0000">*</span></label>
              <div class="col-lg-3">
                <input type="text" v-model="contactlist[i].phone"
                       class="form-control m-input&#45;&#45;fixed"
                       placeholder=""/>
              </div>
              <div class="col-lg-1">
                <div data-repeater-delete=""
                     style="margin-left: 25px"
                     v-on:click="deleteContactNode(i)"
                     class="btn-sm btn btn-danger m-btn m-btn--icon m-btn--pill">
                                                  <span style="width: 20px;height: 25px;">
                                                    <!--<i class="la la-trash-o"></i>-->
                                                     <span> 删除</span>
                                             </span>
                </div>
              </div>
              <div class="col-lg-1">
                <div data-repeater-create=""
                     style="width: 55px;margin-left: 15px"
                     v-on:click="addContactNode()"
                     class="btn btn btn-sm btn-brand m-btn m-btn--icon m-btn--pill m-btn--wide">
                                                <span style="width: 20px;height: 25px;margin-left: -13px">
                                                <!--<i class="la la-plus"></i>-->
                                                                           <span> 添加 </span>
                                                </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div style="display: flex;justify-content: space-around;flex-wrap: wrap">
      <el-card style="width:330px;margin-top: 10px;" v-for="(user,index) in users" :key="index"
               v-loading="cardloading[index]">
        <div slot="header" style="text-align: left">
          <span>{{user.nickname}}</span>
          <el-button style="float: right; padding: 3px 0;color: #ff0509" type="text" icon="el-icon-delete"
                     @click="deleteUser(user.id)">删除
          </el-button>
        </div>
        <div>
          <div><img :src="user.userface" :alt="user.nickname" style="width: 70px;height: 70px"></div>
          <div style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px">
            <span>用户名:</span><span>{{user.username}}</span>
          </div>
          <div style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px">
            <span>电子邮箱:</span><span>{{user.email}}</span>
          </div>
          <div style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px">
            <span>注册时间:</span><span>{{user.regTime | formatDateTime}}</span>
          </div>
          <div
            style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px;display: flex;align-items: center">
            <span>用户状态:</span>
            <el-switch
              v-model="user.enabled"
              active-text="启用"
              active-color="#13ce66"
              @change="enabledChange(user.enabled,user.id,index)"
              inactive-text="禁用" style="font-size: 12px">
            </el-switch>
          </div>
          <div style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px">
            <span>用户角色:</span>
            <el-tag
              v-for="role in user.roles"
              :key="role.id"
              size="mini"
              style="margin-right: 8px"
              type="success">
              {{role.name}}
            </el-tag>
            <el-popover
              placement="right"
              title="角色列表"
              width="200"
              :key="index+''+user.id"
              @hide="saveRoles(user.id,index)"
              trigger="click" v-loading="eploading[index]">
              <el-select v-model="roles" :key="user.id" multiple placeholder="请选择" size="mini">
                <el-option
                  v-for="(item,index) in allRoles"
                  :key="user.id+'-'+item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
              <el-button type="text" icon="el-icon-more" style="padding-top: 0px" slot="reference"
                         @click="showRole(user.roles,user.id,index)"></el-button>
            </el-popover>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>
<script>
  import {getEsRequest, getRequest} from '../utils/api'
  import {putRequest} from '../utils/api'
  import {deleteRequest} from '../utils/api'
  export default{
    mounted: function () {
      this.loading = true;
      this.loadUserList();
      this.cardloading = Array.apply(null, Array(20)).map(function (item, i) {
        return false;
      });
      this.eploading = Array.apply(null, Array(20)).map(function (item, i) {
        return false;
      });
    },
    methods: {
      saveRoles(id, index){
        var selRoles = this.roles;
        if (this.cpRoles.length == selRoles.length) {
          for (var i = 0; i < this.cpRoles.length; i++) {
            for (var j = 0; j < selRoles.length; j++) {
              if (this.cpRoles[i].id == selRoles[j]) {
                selRoles.splice(j, 1);
                break;
              }
            }
          }
          if (selRoles.length == 0) {
            return;
          }
        }
        var _this = this;
        _this.cardloading.splice(index, 1, true)
        putRequest("/admin/user/role", {rids: this.roles, id: id}).then(resp=> {
          if (resp.status == 200 && resp.data.status == 'success') {
            _this.$message({type: resp.data.status, message: resp.data.msg});
            _this.loadOneUserById(id, index);
          } else {
            _this.cardloading.splice(index, 1, false)
            _this.$message({type: 'error', message: '更新失败!'});
          }
        }, resp=> {
          _this.cardloading.splice(index, 1, false)
          if (resp.response.status == 403) {
            var data = resp.response.data;
            _this.$message({type: 'error', message: data});
          }
        });
      },
      showRole(aRoles, id, index){
        this.cpRoles = aRoles;
        this.roles = [];
        this.loadRoles(index);
        for (var i = 0; i < aRoles.length; i++) {
          this.roles.push(aRoles[i].id);
        }
      },
      deleteUser(id){
        var _this = this;
        this.$confirm('删除该用户, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.loading = true;
          deleteRequest("/admin/user/" + id).then(resp=> {
            if (resp.status == 200 && resp.data.status == 'success') {
              _this.$message({type: 'success', message: '删除成功!'})
              _this.loadUserList();
              return;
            }
            _this.loading = false;
            _this.$message({type: 'error', message: '删除失败!'})
          }, resp=> {
            _this.loading = false;
            _this.$message({type: 'error', message: '删除失败!'})
          });
        }).catch(() => {
          _this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      enabledChange(enabled, id, index){
        var _this = this;
        _this.cardloading.splice(index, 1, true)
        putRequest("/admin/user/enabled", {enabled: enabled, uid: id}).then(resp=> {
          if (resp.status != 200) {
            _this.$message({type: 'error', message: '更新失败!'})
            _this.loadOneUserById(id, index);
            return;
          }
          _this.cardloading.splice(index, 1, false)
          _this.$message({type: 'success', message: '更新成功!'})
        }, resp=> {
          _this.$message({type: 'error', message: '更新失败!'})
          _this.loadOneUserById(id, index);
        });
      },
      loadRoles(index){
        var _this = this;
        _this.eploading.splice(index, 1, true)
        getRequest("/admin/roles").then(resp=> {
          _this.eploading.splice(index, 1, false)
          if (resp.status == 200) {
            _this.allRoles = resp.data;
          } else {
            _this.$message({type: 'error', message: '数据加载失败!'});
          }
        }, resp=> {
          _this.eploading.splice(index, 1, false)
          if (resp.response.status == 403) {
            var data = resp.response.data;
            _this.$message({type: 'error', message: data});
          }
        });
      },
      loadOneUserById(id, index){
        var _this = this;
        getRequest("/admin/user/" + id).then(resp=> {
          _this.cardloading.splice(index, 1, false)
          if (resp.status == 200) {
            _this.users.splice(index, 1, resp.data);
          } else {
            _this.$message({type: 'error', message: '数据加载失败!'});
          }
        }, resp=> {
          _this.cardloading.splice(index, 1, false)
          if (resp.response.status == 403) {
            var data = resp.response.data;
            _this.$message({type: 'error', message: data});
          }
        });
      },
      loadUserList(){
        var _this = this;
        getRequest("/admin/user?nickname="+this.keywords).then(resp=> {
          _this.loading = false;
          if (resp.status == 200) {
            _this.users = resp.data;
          } else {
            _this.$message({type: 'error', message: '数据加载失败!'});
          }
        }, resp=> {
          _this.loading = false;
          if (resp.response.status == 403) {
            var data = resp.response.data;
            _this.$message({type: 'error', message: data});
          }
        });
      },
      searchClick(){
        this.loading = true;
        this.loadUserList();
      },
      //监听联系人变化
      onchangContactPersonName:function (i) {
        var name=this.contactlist[i].name;
        alert("联系人名称已输入!"+name);
        getRequest("/admin/user/keyword?nickname="+name).then(resp=> {
          this.words = resp.data;
          //如果药品名称搜索为空，则给出提示
          if(this.words.length<1){
            alert("没有您要搜索的联系人!");
            this.contactlist[i].name='';//清空输入的内容
          }else {
            this.contactlist[i].isShow=true;//显示药品下拉框
          }


        }).catch(function(response) {
          alert("调用后台接口失败!"+response);
        });
      },

//单个联系人选项点击事件
      click_item:function(w,i) {
        debugger
        this.contactlist[i].id=w.ID;
        this.contactlist[i].name=w.NAME;
        this.contactlist[i].isShow=false;
        // 校验联系人名称是否已经输入
        this.VerifyContactName(i);
      },

//校验联系人名称，在数组中是否已经存在
      VerifyContactName:function (i) {
        var flag=true;
        var tempId=this.contactlist[i].id;
        for(var j=0;j<i;j++){
          if(this.contactlist[j].id==tempId){
            flag=false;
          }
        }
        if(flag==false){
          alert("联系人名称已输入!");
          this.contactlist[i].id=''
          this.contactlist[i].name='';//清空输入的内容
          return false;
        }else {
          return true;
        }
      }
    },
    data(){
      return {
        loading: false,
        eploading: [],
        cardloading: [],
        keywords: '',
        users: [],
        allRoles: [],
        roles: [],
        cpRoles: [],
      //联系人数组
      contactlist:[
        {id:'',name: '', phone: '',isShow:false}
      ],

        words: [],//联系人名搜索数组
      }
    }
  }
</script>
