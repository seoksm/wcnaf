import { WiniBox, WiniChip, WiniTypography } from '@/shared/ui/wini';

const BADGE_COLOR = {
  good: 'success',
  warn: 'warning',
  critical: 'error',
  neutral: 'default',
};

const NoteCallout = ({ note }) => {
  if (!note) return null;
  const isWarn = note.type === 'warn';
  return (
    <WiniBox
      className={`mt-3 rounded border border-solid px-3 py-2 text-sm ${
        isWarn ? 'border-red-200 bg-red-50 text-red-700' : 'border-brand-main/25 bg-brand-main/5 text-text-default'
      }`}
    >
      <WiniTypography variant="span" className={`mb-1 block text-xs font-bold ${isWarn ? 'text-red-600' : 'text-brand-main'}`}>
        {isWarn ? '주의 — 되돌릴 수 없음' : '참고'}
      </WiniTypography>
      {note.text}
    </WiniBox>
  );
};

const ManualBlock = ({ block }) => (
  <WiniBox className="mt-4">
    {block.heading && (
      <WiniTypography variant="span" className="mb-1.5 block text-sm font-bold text-text-main">
        {block.heading}
      </WiniTypography>
    )}

    {block.body && (
      <WiniTypography variant="span" className="block text-sm text-text-default">
        {block.body}
      </WiniTypography>
    )}

    {block.steps?.length > 0 && (
      <WiniBox className="flex flex-col gap-1.5 text-sm text-text-default">
        {block.steps.map((step, idx) => (
          <WiniBox key={idx} className="flex gap-2">
            <WiniTypography variant="span" className="flex-none font-mono text-xs font-bold text-brand-main">
              {idx + 1}.
            </WiniTypography>
            <WiniTypography variant="span">{step}</WiniTypography>
          </WiniBox>
        ))}
      </WiniBox>
    )}

    {block.bullets?.length > 0 && (
      <WiniBox className="flex flex-col gap-1.5 text-sm text-text-default">
        {block.bullets.map((bullet, idx) => (
          <WiniBox key={idx} className="flex gap-2">
            <WiniTypography variant="span" className="flex-none text-text-sub">–</WiniTypography>
            <WiniTypography variant="span">{bullet}</WiniTypography>
          </WiniBox>
        ))}
      </WiniBox>
    )}

    {block.table && (
      <WiniBox className="mt-1 overflow-x-auto rounded border border-solid border-gray-200">
        <table className="w-full border-collapse text-sm">
          <thead>
            <tr className="border-b border-solid border-gray-200 bg-gray-50 text-left text-text-sub">
              {block.table.headers.map((h) => (
                <th key={h} className="px-3 py-2 font-semibold">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {block.table.rows.map((row, rIdx) => (
              <tr key={rIdx} className="border-b border-solid border-gray-100 last:border-b-0">
                {row.map((cell, cIdx) => (
                  <td key={cIdx} className="px-3 py-2 align-top">
                    {cell && typeof cell === 'object' && 'badge' in cell ? (
                      <WiniChip label={cell.label} color={BADGE_COLOR[cell.badge] || 'default'} size="small" />
                    ) : (
                      cell
                    )}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </WiniBox>
    )}

    <NoteCallout note={block.note} />
  </WiniBox>
);

/**
 * 매뉴얼 섹션 1개를 렌더링한다. AssetManualDialog의 콘텐츠 영역에서 사용.
 */
export const ManualSectionView = ({ section }) => {
  if (!section) return null;

  return (
    <WiniBox>
      <WiniBox ui="noAutoGap" className="mb-1 inline-block rounded bg-brand-main/10 px-2 py-0.5 font-mono text-xs font-semibold text-brand-main">
        {section.code}
      </WiniBox>
      <WiniTypography variant="h3" className="text-lg font-bold text-text-main">
        {section.title}
      </WiniTypography>
      {section.purpose && (
        <WiniTypography variant="span" className="mt-1.5 block text-sm text-text-sub">
          {section.purpose}
        </WiniTypography>
      )}

      {section.blocks?.map((block, idx) => <ManualBlock key={idx} block={block} />)}

      <NoteCallout note={section.note} />
    </WiniBox>
  );
};
