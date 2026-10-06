import { useRef } from 'react';
import { WiniButton } from '@/shared/ui/wini';

/**
 * capture="environment"로 카메라 촬영을 강제한다(앨범 선택 차단, C-201). 버튼 클릭이 iOS
 * Safari가 요구하는 "사용자 제스처"가 되도록, input을 숨기고 버튼 클릭으로 클릭을 위임한다.
 */
export const PhotoCaptureButton = ({ label = '사진 촬영', onCapture, isUploading, disabled }) => {
  const inputRef = useRef(null);

  const handleChange = (e) => {
    const file = e.target.files?.[0];
    e.target.value = '';
    if (file) onCapture?.(file);
  };

  return (
    <>
      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        capture="environment"
        className="hidden"
        onChange={handleChange}
      />
      <WiniButton
        ui="lineGray"
        onClick={() => inputRef.current?.click()}
        loading={isUploading}
        disabled={isUploading || disabled}
      >
        {label}
      </WiniButton>
    </>
  );
};
