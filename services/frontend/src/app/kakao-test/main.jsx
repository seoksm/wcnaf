import { createRoot } from 'react-dom/client';
import { ThemeProvider } from '@mui/material/styles';
import { appTheme } from '@/shared/config/theme';
import KakaoTestPage from '@/pages/kakao-test';
import { GlobalLayout } from '../layouts/GlobalLayout';
import '../../main.css';
import '../styles/index.css';

createRoot(document.getElementById('root')).render(
  <ThemeProvider theme={appTheme}>
    <GlobalLayout><KakaoTestPage /></GlobalLayout>
  </ThemeProvider>,
);
