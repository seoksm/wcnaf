import React, { useState, useEffect  }  from 'react';
import {
  WiniBox,
  WiniButton,
  WiniGridLayout,
  WiniTypography,
  WiniCode,
  WiniList,
  WiniListItem,
  WiniDialog,
  WiniDialogTitle,
  WiniDialogContent,
  WiniDialogContentText,
  WiniButtonGroup,
  WiniIconButton
} from '@/shared/ui/wini';

import { WiniFormEmpty } from '@/shared/ui/blocks/form-layout';
import { CloseRoundedIcon, ArrowDropDownRoundedIcon } from '@/shared/lib';

export default function SnippetButton() {

  const [dialogOpen, setDialogOpen] = useState(false);

  const codeToCopy = 
`<WiniBox ui="btnitem">
  <WiniButton ui="line" onClick={() => setDialogOpen(true)}>대화상자 열기</WiniButton>
</WiniBox>
<WiniDialog
  color=""
  ui="btn"
  open={dialogOpen}
  onClose={() => setDialogOpen(false)}
  btns={
      <WiniButtonGroup ui="list">
          <WiniButton ui="white" onClick={() => setDialogOpen(false)}>취소</WiniButton>
          <WiniButton className="MuiButton-textMaindark" ui="white" onClick={() => setDialogOpen(false)} autoFocus>
              확인
          </WiniButton>
      </WiniButtonGroup>
  }
>

  <WiniDialogTitle>
    대화상자 제목
    <WiniIconButton
      className="MuiButton-DialogClose"
      size="default"
      icon={<CloseRoundedIcon />}
      onClick={() => setDialogOpen(false)}
    >
      <CloseRoundedIcon className='text-white' />
    </WiniIconButton>
  </WiniDialogTitle>
  <WiniDialogContent className="MuiDialogContainer">
    <WiniDialogContentText>
      확인 요청이나 정보를 표시할 때 사용할 수 있는 대화상자입니다.
    </WiniDialogContentText>
  </WiniDialogContent>
</WiniDialog>`;

  const [copyState, setCopyState] = useState('copy');
  
    useEffect(() => {
      let timer;
      if (copyState === 'complete') {
        timer = setTimeout(() => {
          setCopyState('copy');
        }, 2000);
      }
      return () => clearTimeout(timer);
    }, [copyState]);
  
    const handleCopy = async () => {
      try {
        await navigator.clipboard.writeText(codeToCopy);
        setCopyState('complete');
  
        pubUI.toast({
          text: '복사가 완료되었습니다.',
          time: 2000,
          type: 'success'
        });
  
      } catch (error) {
        console.error('복사 실패:', error);
      }
    };

  return (
    <WiniFormEmpty>
      <WiniGridLayout className="pt-8">

        <WiniTypography variant='h1'>팝업</WiniTypography>
        <WiniTypography variant='h2'>팝업</WiniTypography>

        <WiniBox ui="info">
          <WiniTypography variant="span" className="text-md">
            WiniDialog는 사용자와 상호작용하는 대화상자(Dialog)를 구현하기 위한 컴포넌트입니다.
            대화상자는 일반적으로 확인 요청, 정보 표시, 사용자 입력 등을 위해 사용됩니다.
            <br /><br />
            color="caution"을 사용하여 경고 스타일의 대화상자를 만들 수 있으며, color="error"를 사용하여 오류 스타일의 대화상자를 만들 수 있습니다. <br />
             ui="btn"을 사용하여 버튼형 대화상자를 만들 수 있습니다. <br />
             버튼형 대화상자는 일반적으로 확인과 취소 버튼이 포함된 대화상자로, 사용자에게 선택을 요구하는 상황에서 사용됩니다. <br />
          </WiniTypography>
        </WiniBox>

        <WiniBox>
          {/* 팝업 영역 */}
          <WiniBox ui="btnitem">
            <WiniButton ui="line" onClick={() => setDialogOpen(true)}>대화상자 열기</WiniButton>
          </WiniBox>
          <WiniDialog
            color=""
            ui="btn"
            open={dialogOpen}
            onClose={() => setDialogOpen(false)}
            btns={
                <WiniButtonGroup ui="list">
                    <WiniButton ui="white" onClick={() => setDialogOpen(false)}>취소</WiniButton>
                    <WiniButton className="MuiButton-textMaindark" ui="white" onClick={() => setDialogOpen(false)} autoFocus>
                        확인
                    </WiniButton>
                </WiniButtonGroup>
            }
          >

            <WiniDialogTitle>
              대화상자 제목
              <WiniIconButton
                className="MuiButton-DialogClose"
                size="default"
                icon={<CloseRoundedIcon />}
                onClick={() => setDialogOpen(false)}
              >
                <CloseRoundedIcon className='text-white' />
              </WiniIconButton>
            </WiniDialogTitle>
            <WiniDialogContent className="MuiDialogContainer">
              <WiniDialogContentText>
                확인 요청이나 정보를 표시할 때 사용할 수 있는 대화상자입니다.
              </WiniDialogContentText>
            </WiniDialogContent>
          </WiniDialog>
        </WiniBox>

        

        <WiniBox className='bg-[#1A1A1A] p-6 rounded-sm relative'>
          <WiniBox className='flex items-center justify-between'>
            <WiniTypography variant='span' className='text-white text-lg'>코드 예시</WiniTypography>
            <WiniBox ui="btnbox">
              <WiniButton ui="gray" onClick={handleCopy} className='transition-all duration-300'>
                {copyState === 'copy' ? '복사하기' : '복사 완료'}
              </WiniButton>
            </WiniBox>
          </WiniBox>
          <WiniCode code={codeToCopy} language="jsx" />

        </WiniBox>

        

      </WiniGridLayout>
    </WiniFormEmpty>
  );
}
