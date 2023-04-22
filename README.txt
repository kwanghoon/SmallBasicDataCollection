1) 스몰베이직 프로그램 목록 수집
 - 마이크로소프트 스몰베이직 커뮤니티 
   https://learn.microsoft.com/api/attachments/273808-idnos-20200426.txt?platform=QnA

2) 수집한 목록을 가공
 - smallbasic-program-list

3) 두 가지 스몰베이직 프로그램 목록
 - 학습을 위한 프로그램 목록 : smallbasic-program-list.txt
 - 테스트를 위한 프로그램 목록 : smallbasic-tutorial-list.txt

4) smallbasic-program-list.txt 목록의 스몰베이직 프로그램 수집 
 - java -cp src.com.syntax TextExtract data/smallbasic-program-list.txt ./smallbasic-programs

5) YAPB 프로그램을 통해 smallbasic-program-list 목록의 
   스몰베이직 프로그램들의 구문 완성 데이터를 수집 
 - find 
      ./smallbasic-programs                          (지정한 폴더에서)
      -name "*.sb"                                       (.sb 확장자 파일을 찾아)
      -print -exec stack exec - sbparser-exe \{\} \;   (각각 sbparser-exe를 실행)
      > ./data/smallbasic-program-list-yapb-data-collection_results.txt

   (참고) yapb.config에서 config_COLLECT=True 로 설정해야함 

   YAPB 프로그램을 통해 smallbasic-tutorial-list 목록의 
   스몰베이직 프로그램들의 구문 완성 데이터를 수집 

 - find 
      ./Sample                                         (MySmallBasic 아래의 지정한 폴더에서)
      -name "[0-9][0-9]*.sb"                           (.sb 확장자 파일을 찾아)
      -print -exec stack exec - sbparser-exe \{\} \;   (각각 sbparser-exe를 실행)
      > ./data/smallbasic-tutorial-list-yapb-data-collection_results.txt

   (참고) yapb.config에서 config_COLLECT=True 로 설정해야함 

 5)단계의 결과 (data 폴더 파일 2개)
  - smallbasic-program-list-yapb-data-collection_results.txt
  - smallbasic-tutorial-list-yapb-data-collection_results.txt

   (참고) 구문 완성 후보 데이터와 각종 에러 메시지가 뒤섞여 있음

6) 구문 완성 후보 데이터를 해쉬 맵으로 자료구조화
  - 해쉬맵 : 파싱 상태 --> 구문 완성 후보 리스트 (후보 심볼 리스트와 빈도)
     java -cp src.com.syntax SyntaxCompletionDataManager data/smallbasic-program-list-yapb-data-colletion_results.txt

  - src.com.syntax.SyntaxCompletionDataManager 클래스
  	입력 args:
  		args[0]: smallbasic-program-list-yapb-data-colletion_results.txt 경로
  		
  - 이 클래스 함수들
     * void buildSyntaxCompletionData() 
        : 입력 smallbasic-program-list-yapb-data-collection_results.txt
        : 에러 메시지들은 모두 스킵하고, 수집한 구문 완성 후보와 빈도들을 파싱 상태별로 모음
        : 출력 해쉬맵

     * void listForSyntaxCompletion()
        : 해쉬맵 내용을 화면에 출력하는 함수

     * int searchForSyntaxCompletion(ArrayList<String> arr, int state)
        : 튜토리얼 프로그램에서 얻은 각 구문 완성 후보를 arr로 얻어와
          구문 완성 후보 목록에 포함되어 있는지, 포함되어 있다면 몇 번째에 있는지 확인
          src.com.syntax PerformanceAnalysis 클래스에 적용

     * ArrayList<Pair> searchForSyntaxCompletion(ArrayList<Integer> stateList)
        : MySmallBasic의 UI에서 호출 (ctrl + space를 친 위치 상태 state 리스트 입력받아
          해당 상태의 구문 완성 후보 리스트를 반환 -> pair{구문 후보, 빈도수}) 
          
     * ArrayList<String> mapToArray(Map<ArrayList<String>, Integer> sortList)
     	: 각 상태에 대해 저장한 구문후보와 빈도수를 저장했던 set을 입력으로 받아와
     	  빈도수가 높은 것부터 우선적으로 출력하도록 정렬(내림차순 정렬)
     	  내림차순 정렬 후 ArrayList에 저장하며 빈도수는 제거하여 반환
    
       smallbasic-syntax-completion-candidates-results.txt

 7) 튜토리얼 프로그램 작성에서 수집한 구문 완성 후보 해쉬맵을 적용하여 평가

   - java -cp src.com.syntax PerformanceAnalysis data/smallbasic-tutorial-list-yapb-data-collection_results.txt 
   												 data/smallbasic-program-list-yapb-data-colletion_results.txt

   - 튜토리얼 프로그램에서 각 위치별 구문 완성 후보가 
      스몰베이직 프로그램에서 얻은 데이터 구문 완성 후보 목록에서 포함되어 있는지
      포함되어 있다면 몇번째에 있는지를 확인 

  - src.com.syntax PerformanceAnalysis 클래스
  	입력 args:
  		args[0]: smallbasic-tutorial-list-yapb-data-collection_results.txt 경로
  		args[1]: smallbasic-program-list-yapb-data-colletion_results.txt 경로
  
  - 이 클래스 함수들
   * void buildSyntaxData()
   	  : 입력 smallbasic-tutorial-list-yapb-data-collection_results.txt
   	  : 수집된 구문 완성 후보들을 list로 저장

   * void searchForSyntax(ArrayList<String> list, String path)
      : 입력 void buildSyntaxData()에서 만들어진 list, args[1]
      : 파싱 상태 번호와 빈도를 제외한 구문 완성 후보만을 뽑아
        src.com.syntax.SyntaxCompletionDataManager 클래스의 
        int searchForSyntaxCompletion(ArrayList<String> arr, int state)의 arr에 적용
        스몰베이직 프로그램에서 얻은 데이터 구문 완성 후보 목록에서 포함되어 있는지 확인
        
      
      
      
      
      