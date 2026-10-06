import { styled } from '@mui/material/styles';
import { WiniDrawer, WiniListItem } from '@/shared/ui/wini';

export const drawerWidth = 280;
export const barHeight = 45;

export const openedMixin = (theme) => ({
  width: drawerWidth,
  transition: theme.transitions.create('width', {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.enteringScreen,
  }),
  borderTop: 1,
  overflowX: 'hidden',
});

export const closedMixin = (theme) => ({
  transition: theme.transitions.create('width', {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  borderTop: '0px !important',
  overflowX: 'hidden',
  width: `calc(${theme.spacing(0)})`,
  [theme.breakpoints.up('xs')]: {
    width: `calc(${theme.spacing(0)})`,
  },
  [theme.breakpoints.up('sm')]: {
    width: `calc(${theme.spacing(7)} )`,
  },
});

export const DrawerHeader = styled('div')(({ theme }) => ({
  display: 'flex',
  background: '#eff2f8',
  alignItems: 'center',
  justifyContent: 'flex-end',
  padding: theme.spacing(0, 1),
  // NOTE:
  // - This header is intended to be compact (barHeight = 45).
  // - `theme.mixins.toolbar` injects responsive minHeight (56~64px) and can make it look "too tall".
  minHeight: 45,
  position: 'sticky',
  top: 0,
  zIndex: 1,
}));

// NOTE: `open`은 여기서 온전히 real MUI Drawer로 전달돼야 한다("변형=temporary"일 때
// Modal의 실제 열림/닫힘 여부를 좌우하는 진짜 기능 prop). variant="permanent"에서는
// 항상 렌더링되어 open 값이 표시 여부에 영향을 주지 않으므로 지금까지는 이 값을
// shouldForwardProp으로 걸러내도 문제가 드러나지 않았을 뿐이다.
export const Drawer = styled(WiniDrawer)(({ theme, open }) => ({
  maxWidth: drawerWidth,
  flexShrink: 0,
  whiteSpace: 'nowrap',
  boxSizing: 'border-box',
  overflow: 'hidden',
  boxShadow: 'none',
  ...(open && {
    ...openedMixin(theme),
    '& .MuiDrawer-paper': openedMixin(theme),
  }),
  ...(!open && {
    ...closedMixin(theme),
    '& .MuiDrawer-paper': closedMixin(theme),
  }),
}));

export const Main = styled('main', {
  shouldForwardProp: (prop) => prop !== 'open' && prop !== 'mobile',
})(({ theme, open, mobile }) => ({
  flexGrow: 1,
  ...(mobile
    ? {
        // 모바일에서는 드로어가 오버레이(temporary)라 공간을 차지하지 않으므로,
        // 데스크톱용 음수 마진 보정 없이 항상 전체 너비를 그대로 쓴다.
        padding: theme.spacing(0, 1.5),
        marginLeft: 0,
      }
    : {
        padding: theme.spacing(0, 0, 0, 45),
        transition: theme.transitions.create('margin', {
          easing: theme.transitions.easing.sharp,
          duration: theme.transitions.duration.leavingScreen,
        }),
        marginLeft: `-${drawerWidth + 23}px`,
        ...(open && {
          transition: theme.transitions.create('margin', {
            easing: theme.transitions.easing.easeOut,
            duration: theme.transitions.duration.enteringScreen,
          }),
          marginLeft: '-79px',
        }),
      }),
}));

export const CustomListItem = styled(WiniListItem)(({ theme }) => ({
  cursor: 'pointer',
}));
