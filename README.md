# Http Client Spring Boot Starter

Spring Boot application integrates `HttpClient` quickly.

## Quickstart

- Import dependencies

```xml
    <dependency>
        <groupId>com.yookue.springstarter</groupId>
        <artifactId>http-client-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> By default, this starter will auto take effect, you can turn it off by `spring.http-client.enabled = false`

- Configure Spring Boot `application.yml` with prefix `spring.http-client`

```yml
spring:
    http-client:
        sync-client:
            user-agent: 'Apache-HttpClient'
        async-client:
            user-agent: 'Apache-HttpClient'
```

- This starter creates two beans as follows, then you can configure your beans by constructor or `@Autowired`/`@Resource` annotation, then you can access it.

| Bean Name       | Bean Class                                       |
|-----------------|--------------------------------------------------|
| syncHttpClient  | org.apache.hc.client5.http.classic.HttpClient    |
| asyncHttpClient | org.apache.hc.client5.http.async.HttpAsyncClient |

## Document

- Github: https://github.com/yookue/http-client-spring-boot-starter
- HttpClient github: https://github.com/apache/httpcomponents-client
- HttpClient homepage: https://hc.apache.org

## Requirement

- jdk 1.8+

## License

This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

See the `NOTICE.txt` file for required notices and attributions.

## Donation

You like this package? Then [donate to Yookue](https://yookue.com/public/donate) to support the development.

## Website

- Yookue: https://yookue.com
