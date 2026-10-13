import { useUserChangeTheme } from '@/features/user/change-theme';
import { useUserSelectLanguage } from '@/features/user/select-language';
import { useUserUpdateProfile } from '@/features/user/update-profile';
import { useMfaSettings } from '@/features/user/mfa-settings';
import { winiCom } from '@/shared/lib';

export function useMyPage() {
    const { connector } = winiCom.getFormInfo('');

    const themeModel = useUserChangeTheme("system");
    const languageModel = useUserSelectLanguage("ko");
    const updateProfileModel = useUserUpdateProfile(connector);
    const mfaModel = useMfaSettings(connector);

    return {
        themeValue: themeModel.value,
        onThemeChange: themeModel.onChange,

        languageValue: languageModel.value,
        onLanguageChange: languageModel.onChange,

        updateProfileModel: updateProfileModel,
        mfaModel: mfaModel,
    };
}
