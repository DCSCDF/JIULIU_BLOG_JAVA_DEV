# 重构的JAVA后端

## 用户模块接口文档

## 1. 获取 RSA 公钥

用于前端加密登录密码。

- **URL**: `/user/public-key`
- **Method**: `GET`
- **Rate Limit**: 每 800 毫秒最多 10 次（按 IP + 方法限流）
- **Auth Required**: 否

### ✅ 成功响应示例
```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
    }
}
```

---

## 2. 用户登录

前端需使用上述公钥对**明文密码**进行 RSA 加密，并以 Base64 字符串形式提交。

- **URL**: `/user/login`
- **Method**: `POST`
- **Rate Limit**: 每 800 秒最多 10 次（按 IP + 方法限流）
- **Auth Required**: 否
- **Content-Type**: `application/json`

### 🔸 请求体 (LoginDTO)
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | string | 是 | 用户名 |
| `password` | string | 是 | **RSA 加密后的 Base64 密码字符串** |

### ✅ 成功响应示例
```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9.xxxxx",
        "username": "admin",
        "updateTime": "2026-01-05T14:30:00"
    }
}
```

### ❌ 错误响应示例
```json
{
    "code": 401,
    "message": "密码错误",
    "data": null
}
```

> 可能的错误消息：
> - "用户名或密码不能为空"
> - "密码解密失败，请刷新页面重试"
> - "用户名不存在"
> - "密码错误"

---

## 3. 检查是否已登录

- **URL**: `/user/is-logged-in`
- **Method**: `GET`
- **Auth Required**: 否

### ✅ 成功响应示例（已登录）
```json
{
    "code": 200,
    "message": "成功",
    "data": {
    "loggedIn": true
    }
}
```

### ✅ 成功响应示例（未登录）
```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "loggedIn": false
    }
}
```

---

## 4. 获取用户资料（需登录）

返回当前登录用户的基本信息。

- **URL**: `/user/profile`
- **Method**: `GET`
- **Auth Required**: 是自动校验
- **Headers**: 需携带有效 Token（通常在 Cookie 或 Header 中）

### ✅ 成功响应示例
```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "createTime": "2026-01-01T10:00:00",
        "updateTime": "2026-01-05T14:30:00",
        "avatarUrl": "https://example.com/avatar.png"
    }
}
```

### ❌ 错误响应示例（用户不存在）
```json
{
    "code": 401,
    "message": "用户不存在",
    "data": null
}
```

> ⚠️ 若未登录，会抛出 `NotLoginException`，由全局异常处理器返回 401。