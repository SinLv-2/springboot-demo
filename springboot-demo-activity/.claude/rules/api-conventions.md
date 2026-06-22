# API Conventions

- RESTful 风格，资源名使用复数形式
- 统一响应格式：{ data, error, meta }
- 错误码遵循 HTTP 标准语义
- 所有接口需要在 OpenAPI 文档中声明
- 分页参数统一使用 page / page_size