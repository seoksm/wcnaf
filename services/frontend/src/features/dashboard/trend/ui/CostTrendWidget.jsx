import { Line } from 'react-chartjs-2';
import TrendingUpIcon from '@mui/icons-material/TrendingUpOutlined';
import { WiniTypography } from '@/shared/ui/wini';
import winiDate from '@/shared/lib/date';
import { formatAmount } from '@/shared/lib/payment';
import { WidgetCard } from '../../common/ui/WidgetCard';
import { COST_TREND_COLORS } from '../../common/model/chartColors';

const withAlpha = (hex, alpha) => {
  const r = parseInt(hex.slice(1, 3), 16);
  const g = parseInt(hex.slice(3, 5), 16);
  const b = parseInt(hex.slice(5, 7), 16);
  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};

/**
 * S-700 월별 비용 추이 - 누적 영역차트, 귀속월 기준(Q-52/53: 결제일이 아니라 귀속월로 집계).
 * "기타" 계열은 렌탈·라이선스 외 지출원장이 없어(스키마 확인됨) 2계열만 표시한다(스코프 축소).
 */
export const CostTrendWidget = ({ items }) => {
  const rows = items ?? [];

  const chartData = {
    labels: rows.map((row) => winiDate.dateFormat(winiDate(row.month), 'YYYY-MM')),
    datasets: [
      {
        label: '렌탈',
        data: rows.map((row) => row.rentalAmount ?? 0),
        borderColor: COST_TREND_COLORS.rental,
        backgroundColor: withAlpha(COST_TREND_COLORS.rental, 0.25),
        fill: 'origin',
        tension: 0.25,
        pointRadius: 2,
      },
      {
        label: '라이선스',
        data: rows.map((row) => row.licenseAmount ?? 0),
        borderColor: COST_TREND_COLORS.license,
        backgroundColor: withAlpha(COST_TREND_COLORS.license, 0.25),
        fill: '-1',
        tension: 0.25,
        pointRadius: 2,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    interaction: { mode: 'index', intersect: false },
    plugins: {
      legend: { position: 'top', align: 'end', labels: { boxWidth: 10, boxHeight: 10 } },
      tooltip: { callbacks: { label: (ctx) => `${ctx.dataset.label}: ${formatAmount(ctx.parsed.y)}` } },
    },
    scales: {
      y: { stacked: true, beginAtZero: true, ticks: { callback: (v) => formatAmount(v) } },
      x: { grid: { display: false } },
    },
  };

  return (
    <WidgetCard icon={TrendingUpIcon} title="월별 비용 추이">
      {rows.length === 0 ? (
        <WiniTypography variant="span" className="text-xs text-text-sub">데이터가 없습니다.</WiniTypography>
      ) : (
        <div style={{ height: 260 }}>
          <Line data={chartData} options={options} />
        </div>
      )}
    </WidgetCard>
  );
};
