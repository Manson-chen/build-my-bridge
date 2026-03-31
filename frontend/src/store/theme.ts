import { create } from 'zustand'

interface ThemeStore {
  themeMode: 'light' | 'dark'
  toggleTheme: () => void
}

export const useThemeStore = create<ThemeStore>((set) => ({
  themeMode: (localStorage.getItem('theme') as 'light' | 'dark') || 'light',
  toggleTheme: () =>
    set((state) => {
      const newTheme = state.themeMode === 'light' ? 'dark' : 'light'
      localStorage.setItem('theme', newTheme)
      return { themeMode: newTheme }
    }),
}))
