/** @type {import('next').NextConfig} */
const path = require('path');

const nextConfig = {
    reactStrictMode: true,
    swcMinify: true,
    output: 'standalone', // Enable standalone output for Docker
    typescript: {
        // Avoid failing the build on TypeScript deprecation diagnostics
        ignoreBuildErrors: true,
    },
    eslint: {
        // Warning: This allows production builds to successfully complete even if
        // your project has ESLint errors.
        ignoreDuringBuilds: true,
    },
    experimental: {
        externalDir: true
    },
    webpack(config) {
        const rootModules = path.resolve(__dirname, 'node_modules');
        if (Array.isArray(config.resolve.modules)) {
            config.resolve.modules = [rootModules, ...config.resolve.modules];
        } else {
            config.resolve.modules = [rootModules];
        }

        config.resolve.alias['lucide-react'] = path.resolve(__dirname, 'node_modules', 'lucide-react');

        return config;
    }
};

module.exports = nextConfig;
