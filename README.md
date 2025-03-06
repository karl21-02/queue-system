# 티켓 접속자 대기열 시스템

## 개요
이 프로젝트는 **Spring WebFlux**와 **Reactive Redis**를 활용하여 티켓 접속자 대기열 시스템을 구현한 것입니다. 많은 사용자가 동시에 특정 서비스에 접속할 때 부하를 관리하고 순차적으로 입장할 수 있도록 대기열을 운영합니다.

## 주요 기능
- **대기열 등록**: 사용자가 접속을 시도하면 Redis SortedSet을 활용하여 대기열에 등록됩니다.
- **순위 확인**: 사용자의 대기열 내 위치를 실시간으로 조회할 수 있습니다.
- **입장 허용**: 특정 조건을 만족하는 사용자가 입장할 수 있도록 제어합니다.
- **대기열 이탈**: 쿠키를 사용하여 사용자가 대기열을 이탈할 수 있도록 지원합니다.
- **대기열 스케줄러**: 주기적으로 대기열을 관리하고 불필요한 데이터를 정리합니다.

## 기술 스택
- **Backend**: Spring Boot, Spring WebFlux
- **Database**: Redis (Reactive Redis)
- **test**: jmeter

## API & 기능
- **쿠키를 사용한 대기열 이탈 기능 추가**
- **대기열 스케줄러 개발**
- **진입 요청 API 개발**
- **REDIS - 대기열 등록 API 구현**
