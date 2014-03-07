namespace bug242417 {   
    
    struct CastStruct242417 {
        CastStruct242417(int a) {}
    };
    typedef CastStruct242417 CSAlias242417;

    struct AAA1_242417 {
        template <class T> 
        using BBB1 = T;

        operator CSAlias242417();

        int xx;
        AAA1_242417() {xx = 1;}
    };

    AAA1_242417::operator BBB1<CastStruct242417>() {
        return xx + 10;
    }     
 
    struct AAA2_242417 {    
        operator CSAlias242417();    
    };

    AAA2_242417::operator CastStruct242417() { // click on operator doesn't navigate to the declaration
        return AAA2_242417();
    }
    
    namespace DDD_242417 { 

        struct Test_242417 {};

        typedef Test_242417 TEST_242417;

        struct AAA3_242417 {
            operator Test_242417();
        };
    }     


    DDD_242417::AAA3_242417::operator DDD_242417::TEST_242417() {
        return Test_242417(); 
    }       
}