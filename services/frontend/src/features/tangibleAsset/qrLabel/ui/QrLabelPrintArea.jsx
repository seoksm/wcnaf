import { createPortal } from 'react-dom';
import { useEffect, useState } from 'react';

const PORTAL_ID = 'qr-print-root';

/**
 * 인쇄 시에만 나타나는 QR 라벨 그리드 (S-216)
 * #root(사이드바·헤더 등 앱 전체) 바깥으로 포탈 렌더링해, 인쇄 시 다른 화면 요소가 함께
 * 출력되지 않도록 한다. #root를 숨기는 규칙은 main.css의 @media print에 전역으로 정의되어 있다.
 */
export const QrLabelPrintArea = ({ labels }) => {
  const [portalNode, setPortalNode] = useState(null);

  useEffect(() => {
    const existingNode = document.getElementById(PORTAL_ID);
    const node = existingNode || document.createElement('div');
    if (!existingNode) {
      node.id = PORTAL_ID;
      document.body.appendChild(node);
    }
    setPortalNode(node);

    return () => {
      if (!existingNode && node.parentNode) node.parentNode.removeChild(node);
    };
  }, []);

  if (!portalNode || !labels || labels.length === 0) return null;

  return createPortal(
    <div className="hidden print:grid print:grid-cols-3 print:gap-4 print:p-4">
      {labels.map((label) => (
        <div
          key={label.tangibleAssetId}
          className="flex flex-col items-center border border-solid border-gray-300 p-2 break-inside-avoid"
        >
          <img src={label.qrDataUrl} alt={label.assetCode} className="w-32 h-32" />
          <div className="text-xs font-bold mt-1">{label.assetCode}</div>
          <div className="text-xs">{label.assetName}</div>
        </div>
      ))}
    </div>,
    portalNode,
  );
};
