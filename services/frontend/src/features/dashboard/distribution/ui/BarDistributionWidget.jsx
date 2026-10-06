import { Bar } from 'react-chartjs-2';
import CategoryIcon from '@mui/icons-material/CategoryOutlined';
import { WiniTypography } from '@/shared/ui/wini';
import { WidgetCard } from '../../common/ui/WidgetCard';
import { CATEGORY_COLORS } from '../../common/model/chartColors';

/**
 * S-700 종류별/위치별 현황 - 값 내림차순 가로 막대(설계문서 §1: "종류 7개를 파이로 그리면 순위
 * 비교가 불가능하다"). 데이터는 이미 백엔드에서 내림차순 + 7범주 상한(상위6+기타)로 정리돼 온다.
 */
export const BarDistributionWidget = ({ icon = CategoryIcon, title, items }) => {
  const rows = items ?? [];

  if (rows.length === 0) {
    return (
      <WidgetCard icon={icon} title={title}>
        <WiniTypography variant="span" className="text-xs text-text-sub">데이터가 없습니다.</WiniTypography>
      </WidgetCard>
    );
  }

  const chartData = {
    labels: rows.map((row) => row.name),
    datasets: [{
      data: rows.map((row) => row.count),
      backgroundColor: rows.map((_, index) => CATEGORY_COLORS[index % CATEGORY_COLORS.length]),
      borderRadius: 4,
      barThickness: 16,
    }],
  };

  const options = {
    indexAxis: 'y',
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: { callbacks: { label: (ctx) => `${ctx.label}: ${ctx.parsed.x}건` } },
    },
    scales: {
      x: { beginAtZero: true, ticks: { precision: 0 } },
      y: { grid: { display: false } },
    },
  };

  return (
    <WidgetCard icon={icon} title={title}>
      <div style={{ height: Math.max(120, rows.length * 34) }}>
        <Bar data={chartData} options={options} />
      </div>
    </WidgetCard>
  );
};
