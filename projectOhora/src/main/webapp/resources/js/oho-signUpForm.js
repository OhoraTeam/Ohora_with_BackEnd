function pwdcError() {
    let pwdAlert = $("#pwConfirmMsg");
    pwdAlert.text("");
    if (pwdAlert.hasClass("error")) {
        pwdAlert.text("비밀번호가 일치하지 않습니다")
    }
}


$(document).on('click', function(event) {
    //alert('페이지의 아무 곳이나 클릭했습니다!')
    let pwd = $("#passwd").val();
    let pwdc = $("#passwd-confirm").val();
    //alert(pwd)
    if (pwdc!=pwd) {
        $("#pwConfirmMsg").addClass("error")
    }else {
        $("#pwConfirmMsg").removeClass("error")
    }
    pwdcError();
});


//유효 이메일 여부
//중복 검사 db에서.. 하던가말던가..

function isValidEmail(email) {
    // 이메일 정규 표현식
    var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(email);
}

function emailError() {
    let emailAlert = $("#emailMsg");
    emailAlert.text("");
    if (emailAlert.hasClass("error")) {
        emailAlert.text("유효하지 않은 이메일 형식입니다")
    }
}

$(document).on('click', function(event) {
    //alert('페이지의 아무 곳이나 클릭했습니다!')
    let email = $("#email").val();
    let emailAlert = $("#emailMsg");
    if (!email) {
        emailAlert.text("이메일을 입력하세요");
        return;
    }


    if (!isValidEmail(email)) {
        $("#emailMsg").addClass("error")
    }else {
        $("#emailMsg").removeClass("error")
    }
    emailError();
});

// 전체 동의


function allAgreeBtn() {

        // 전체 선택 체크박스 상태 확인
        const selectAllCheckbox = document.getElementById("AgreeAllCk");
        const checkboxes = document.querySelectorAll(".termCK");

        // 전체 선택 체크박스의 상태에 따라 개별 체크박스 선택/해제
        checkboxes.forEach((checkbox) => {
            checkbox.checked = selectAllCheckbox.checked;
        });

}

function updateSelectAll() {
    const selectAllCheckbox = document.getElementById("AgreeAllCk");
    const checkboxes = document.querySelectorAll(".termCK");

    // 개별 체크박스 중 하나라도 체크 해제된 것이 있는지 확인
    const allChecked = Array.from(checkboxes).every((checkbox) => checkbox.checked);

    // 모든 개별 체크박스가 선택되어 있으면 전체 선택 체크, 아니면 해제
    selectAllCheckbox.checked = allChecked;
}

//회원가입 서브밋(구현안함)

function copy_join_btn() {
    $("#joinForm").submit();
}

//ID 중복 체크
function id_jungbok() {
    
}

//이메일 중복체크
function email_jungbok() {

}

//휴대폰 중복체크 
function phone_jungbok() {

}