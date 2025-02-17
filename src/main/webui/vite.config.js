import { resolve } from 'path';

export default {
  root: resolve(__dirname, 'src'),
  base: '/',
  build: {
    outDir: '../dist'
  },
  server: {
    host: '0.0.0.0',
    port: 5173
  }
};
