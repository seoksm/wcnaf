import { Fragment } from 'react';

import {
  WiniBox,
  WiniCard,
  WiniBreadcrumbs,
  WiniDivider,
  WiniTypography,
  WiniStack,
} from '@/shared/ui/wini';
import { NavigateNextIcon } from '@/shared/lib';
/**
 * WiniFormNormal 화면에 기본적으로 표시되는 부분 표시 (브래드크럼, 타이틀)
 * @param {*}
 * @returns dom
 */
export default function WiniFormNormal({ children, ...props }) {
  // React.useEffect(()=>{
  //     if(props.title===undefined){
  //         console.warn('[Normal Form] title 속성을 입력하세요 title={"title"}')
  //     }
  //     if(props.path===undefined){
  //       console.warn('[Normal Form] path 속성을 입력하세요 path={["경로","타이틀"]}')
  //   }
  // },[])
  // const rendBreadcrumbs = (path, index) => {
  //     return(
  //         <WiniTypography color="text.primary" key = {path+index} > {path}</WiniTypography>
  //     )
  // }
  return (
    <Fragment>
      <WiniBox
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="10vh"
        className="normalForm"
        sx={{
          // p: 1,
          overflowY: 'auto',
          // borderTop:'2px solid #dcdcdc'
          // marginTop: 1,
          // flexGrow:1
          // paddingLeft:1.5
        }}
      >
        {/* <WiniStack direction={'column'} flexGrow={1} sx={{flexGrow:1}} >
                    <WiniBox sx={{ background:'#eff2f8',minHeight:'44px',pl:1}}> WiniBreadcrumbs space
                    {
                        // props.path===undefined?
                        // (<WiniTypography sx={{paddingLeft: 2}}>path</WiniTypography>)
                        // :
                        // (<WiniBreadcrumbs aria-label="breadcrumb" separator={<NavigateNextIcon fontSize="small" />}
                        //     sx={{ paddingLeft: 2 }}
                        // >
                        //     { props.path.map((item, index)=> rendBreadcrumbs(item, index)) }
                        // </WiniBreadcrumbs>
                        // )
                    }
                    </WiniBox>
                    <WiniDivider/> */}
        {/*
          @container: 사이드바(280px, AuthenticatedLayout의 Main)가 뷰포트 폭 중 실제
          쓸 수 있는 폭을 계속 갉아먹기 때문에, 자식이 뷰포트 기준 브레이크포인트(md:/lg:
          등)를 쓰면 실제로는 좁은데 뷰포트만 넓다는 이유로 다단 레이아웃이 눌려 보이는
          문제가 생긴다(사이드바 열림/닫힘 여부에 따라 폭이 또 달라져 고정 보정값으로도
          못 맞춘다). Tailwind v4 컨테이너 쿼리(@sm:/@md:/@lg:)를 여기서 한 번만 열어두면
          이 화면 콘텐츠의 실제 렌더 폭을 기준으로 자식들이 반응할 수 있다.
        */}
        <WiniBox className="@container" sx={{ minHeight: 500, pb: 4, pl: 4, pr: 3, flexGrow: 1 }}>
          {/* <WiniBox
                            sx={{
                                '& .MuiTextField-root': { m: 1 },
                            }}
                        >
                            <WiniBox sx = {{ padding:1, paddingLeft:2, paddingRight:2 }}> */}
          {children} {/* 화면 내용물 */}
          {/* </WiniBox>
                        </WiniBox> */}
        </WiniBox>

        {/* </WiniStack> */}
      </WiniBox>
      {/* <WiniBox
            display="flex"
            justifyContent="center"
            // alignItems="center"
            minHeight="10vh"
            // maxHeight={window.innerHeight-80 }
            sx={{
                p:1,
                // marginTop: 2,
                // paddingTop:2,
                overflowY:'auto',
                // maxHeight:window.innerHeight-80
                // paddingLeft:1.5
            }}
        >
            
        </WiniBox> */}
    </Fragment>
  );
}
